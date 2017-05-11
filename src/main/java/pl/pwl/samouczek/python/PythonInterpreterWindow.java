package pl.pwl.samouczek.python;

import com.vaadin.data.Property;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.event.ShortcutAction;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.python.antlr.PythonLexer;
import org.python.antlr.PythonParser;
import org.python.antlr.PythonTree;
import org.python.antlr.ast.Call;
import org.python.antlr.ast.Expression;
import org.python.antlr.ast.Module;
import org.python.antlr.ast.Name;
import org.python.antlr.base.mod;
import org.python.antlr.runtime.ANTLRStringStream;
import org.python.antlr.runtime.Token;
import org.python.core.*;
import org.python.google.common.collect.Sets;
import org.python.util.PythonInterpreter;
import org.vaadin.aceeditor.AceEditor;
import org.vaadin.aceeditor.AceMode;
import pl.pwl.samouczek.ui.handler.OnEnterKeyHandler;
import pl.pwl.samouczek.ui.handler.OnKeyHandler;
import pl.pwl.samouczek.ui.handler.OnTabKeyHandler;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PythonInterpreterWindow extends VerticalLayout {

    private static final long EXECUTION_TIME = 200;

    public static interface GoalListener {
		public void goalMatched();
	}
	
	private static final long serialVersionUID = 1L;
	private PythonInterpreter interpreter;
	private StringBuilder currentClause = new StringBuilder();
	private int indentLevel = 0;
	private List<String> commandHistory = new ArrayList<>();
	private int commandHistoryPointer = 0;
	private String goal;
	private List<GoalListener> goalListeners = new ArrayList<>();
	
	public void registerGoalListener(GoalListener listener) {
		goalListeners.add(listener);
	}
	
	public void clearGoalListeners() {
		goalListeners.clear();
	}

	public PythonInterpreterWindow() {
		init();
	}
	
	private final static Set<String> illegalFunctions = Sets.newHashSet("apply", "compile", "eval", "execfile", "file", "memoryview", "open", "__import__");
	
	protected boolean hasPythonTreeIllegalCalls(PythonTree tree) {
		if (tree instanceof Call) {
			Call callTree = (Call) tree;
			PythonTree name = callTree.getFirstChildWithType(PythonParser.NAME);
			Name funcName = (Name) name;
			String txt = funcName.getText();
			if (illegalFunctions.contains(txt)) {
				return true;
			}
		} else {
			if (tree == null || tree.getChildren() == null) {
				return false;
			}
			for (PythonTree child : tree.getChildren()) {
				if (hasPythonTreeIllegalCalls(child)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public static String rtrim(String s) {
	    int i = s.length()-1;
	    while (i >= 0 && Character.isWhitespace(s.charAt(i))) {
	        i--;
	    }
	    return s.substring(0,i+1);
	}

	private void init() {
		setMargin(true);
		setHeight("100%");

		final Label ta = new Label();
		ta.setContentMode(ContentMode.HTML);
		
		final Panel panel = new Panel();
		panel.setHeight("90%");

        final HorizontalLayout hl = new HorizontalLayout();
		
		final TextField tf = new TextField();
		tf.addStyleName("code-entry");

        final Button editorButton = new Button("Edytor");

		panel.setContent(ta);
		
		addComponent(panel);

        hl.setWidth("100%");
        hl.addComponent(tf);
        hl.addComponent(editorButton);
        hl.setExpandRatio(tf, 9);
        hl.setExpandRatio(editorButton, 1);
        hl.setComponentAlignment(editorButton, Alignment.MIDDLE_RIGHT);

		addComponent(hl);
		
		setExpandRatio(panel, 4.0f);
		
		final StringWriter sw = new StringWriter();
				
		interpreter = new PythonInterpreter();
		interpreter.setOut(sw);
		interpreter.setErr(sw);

        InterpreterOnEnterKeyHandler kh = new InterpreterOnEnterKeyHandler(tf, panel, ta, sw);

        editorButton.addClickListener((click) -> showEditorWindow(tf, panel, ta, sw, kh));

		OnTabKeyHandler th = new OnTabKeyHandler() {
			@Override
			public void onTabKeyPressed() {
				if (tf.getValue() != null) {
					tf.setValue(tf.getValue() + "\t");
				}
			}
		};
		
		OnKeyHandler uph = new OnKeyHandler() {
			@Override
			public void onKeyPressed() {
				if (commandHistoryPointer > 0) {
					commandHistoryPointer--;
					if (commandHistory.size() > commandHistoryPointer) {
						tf.setValue(commandHistory.get(commandHistoryPointer));
					}
				}
			}
			
			@Override
			protected int getKeyCode() {
				return ShortcutAction.KeyCode.ARROW_UP;
			}
		};
		
		OnKeyHandler downh = new OnKeyHandler() {
			@Override
			public void onKeyPressed() {
				if (commandHistoryPointer < commandHistory.size()) {
					commandHistoryPointer++;
					if (commandHistory.size() > commandHistoryPointer) {
						tf.setValue(commandHistory.get(commandHistoryPointer));
					} else {
						tf.setValue("");
					}
				}
			}
			
			@Override
			protected int getKeyCode() {
				return ShortcutAction.KeyCode.ARROW_DOWN;
			}
		};
		
		kh.installOn(tf);
		th.installOn(tf);
		uph.installOn(tf);
		downh.installOn(tf);
		
	}

    private Property<String> editorText = new ObjectProperty<>("");

    private void showEditorWindow(TextField tf, Panel panel, Label ta, StringWriter sw, InterpreterOnEnterKeyHandler kh) {
        Window window = new Window("Edytor poleceń");
        window.setModal(true);
        window.setWidth("50%");
        window.setHeight("50%");

        VerticalLayout vl = new VerticalLayout();
        vl.setSizeFull();
        AceEditor ae = new AceEditor();
        ae.setSizeFull();
        ae.setMode(AceMode.python);
        ae.setPropertyDataSource(editorText);
        ae.setImmediate(true);
        vl.addComponent(ae);

        Button saveButton = new Button("Zapisz i wykonaj");
        vl.addComponent(saveButton);
        vl.setComponentAlignment(saveButton, Alignment.BOTTOM_CENTER);
        window.setContent(vl);
        getUI().addWindow(window);

        saveButton.addClickListener(clickEvent -> {
            StringBuilder sb = new StringBuilder();
            sb.append(ta.getValue() == null ? "" : ta.getValue());
            sb.append("<pre style='color: #5927C4'>\n");
            sb.append(editorText.getValue());
            sb.append("\n</pre>");
            kh.validateAndExecute(sb, editorText.getValue());
            ta.setValue(sb.toString());
            getUI().removeWindow(window);
        });
    }

    public String getGoal() {
		return goal;
	}

	public void setGoal(String goal) {
		this.goal = goal;
	}

	private class InterpreterOnEnterKeyHandler extends OnEnterKeyHandler {

		private final TextField tf;
		private final Panel panel;
		private final Label ta;
		private final StringWriter sw;

		public InterpreterOnEnterKeyHandler(TextField tf, Panel panel, Label ta, StringWriter sw) {
			this.tf = tf;
			this.panel = panel;
			this.ta = ta;
			this.sw = sw;
		}

		@Override
        public void onEnterKeyPressed() {
            if (tf.getValue() != null) {
                commandHistory.add(tf.getValue());
            }
            commandHistoryPointer = commandHistory.size();
            handleTextField();
            panel.markAsDirty();
            panel.setScrollTop(Integer.MAX_VALUE / 2);
            panel.markAsDirty();
        }

		public void handleTextField() {
            StringBuilder sb = new StringBuilder();
            sb.append(ta.getValue() == null ? "" : ta.getValue());
            if (indentLevel == 0) {
                sb.append("<pre style='color: #5927C4'>\n&gt; ");
            } else if (indentLevel > 0 && tf.getValue() != null && rtrim(tf.getValue()).isEmpty()) {
                sb.append("<pre style='color: #5927C4'>\n|--");
            } else {
                sb.append("<pre style='color: #5927C4'>\n| ");
            }
            sb.append(tf.getValue() == null ? "" : tf.getValue()).append("\n</pre><pre>");
            if (tf.getValue() != null) {
                tf.setValue(rtrim(tf.getValue()));
                String strippedCurrentString = tf.getValue();
                tf.setValue(tf.getValue() + "\n");
                String currentString = tf.getValue();
                int newIndentLevel = 0;
                if (indentLevel > 0) {
                    String currentStringCopy = currentString;
                    while (currentStringCopy.startsWith("\t")) {
                        newIndentLevel++;
                        currentStringCopy = currentStringCopy.substring(1);
                    }
                }
                if (strippedCurrentString.endsWith(":")) {
                    newIndentLevel++;
                    currentClause.append(currentString);
                    tf.setValue("");
                    for (int i = 0; i < newIndentLevel; i++) {
                        tf.setValue(tf.getValue() + "\t");
                    }
                    indentLevel = newIndentLevel;
                    sb.append("</pre>");
                    ta.setValue(sb.toString());
                    return;
                }
                if (newIndentLevel > 0) {
                    currentClause.append(currentString);
                    indentLevel = newIndentLevel;
                    tf.setValue("");
                    for (int i = 0; i < indentLevel; i++) {
                        tf.setValue(tf.getValue() + "\t");
                    }
                    sb.append("</pre>");
                    ta.setValue(sb.toString());
                    return;
                }
                if (indentLevel > 0 && newIndentLevel == 0) {
                    currentClause.append(currentString);
                    currentString = currentClause.toString();
                    currentClause = new StringBuilder();
                }
                indentLevel = newIndentLevel;
                validateAndExecute(sb, currentString);
            }
            sb.append("</pre>");
            ta.setValue(sb.toString());
            tf.setValue("");
        }

		public void validateAndExecute(StringBuilder sb, String currentString) {
            boolean valid = true;
            try {
                ANTLRStringStream stream = new ANTLRStringStream(currentString);
                PythonLexer lexer = new PythonLexer(stream);
                if (lexer.failed()) {
                    sb.append("</pre><pre style='color: red'>Niepoprawna składnia. Sprawdź, czy wszystkie cudzysłowy i nawiasy są poprawnie zamknięte.</pre><pre><br />");
                    valid = false;
                }
                if (valid) {
                    Token token = null;
                    try {
                        token = lexer.nextToken();
                    } catch (Exception e) {
                        sb.append("</pre><pre style='color: red'>Niepoprawna składnia. Sprawdź, czy wszystkie cudzysłowy i nawiasy są poprawnie zamknięte.</pre><pre><br />");
                        valid = false;
                    }
                    while (valid && token != null && token.getType() != -1) {
                        if (token.getType() == PythonLexer.IMPORT) {
                            sb.append("</pre><pre style='color: red'>Importowanie nie jest dozwolone!</pre><pre><br />");
                            valid = false;
                            break;
                        } else if (token.getType() == PythonLexer.EXEC) {
                            sb.append("</pre><pre style='color: red'>Instrukcja 'exec' nie jest dozwolona!</pre><pre><br />");
                            valid = false;
                            break;
                        }
                        try {
                            token = lexer.nextToken();
                        } catch (Exception e) {
                            sb.append("</pre><pre style='color: red'>Niepoprawna składnia. Sprawdź, czy wszystkie cudzysłowy i nawiasy są poprawnie zamknięte.</pre><pre><br />");
                            valid = false;
                            break;
                        }
                    }
                    lexer.reset();
                    mod parse = ParserFacade.parseExpressionOrModule(new StringReader(currentString), "<script>", new CompilerFlags());
                    List<PythonTree> children;
                    if (parse instanceof Expression) {
                        Expression parseExpr = (Expression) parse;
                        children = parseExpr.getChildren();
                    } else if (parse instanceof Module) {
                        Module mod = (Module) parse;
                        children = mod.getChildren();
                    } else {
                        throw new IllegalArgumentException("Nierozpoznane dane: " + parse);
                    }
                    if (children != null) {
                        for (PythonTree child : children) {
                            if (hasPythonTreeIllegalCalls(child)) {
                                sb.append("</pre><pre style='color: red'>Próba wywołania niedozwolonej funkcji!</pre><pre><br />");
                                valid = false;
                                break;
                            }
                        }
                    }
                }

                if (valid) {
                    final MutableBoolean executionFinished = new MutableBoolean(false);
                    Thread t = new Thread() {
                        @Override
                        public void run() {
                            interpreter.exec(currentString);
                            executionFinished.setTrue();
                        }
                    };
                    t.start();
                    try {
                        t.join(EXECUTION_TIME);
                        if (executionFinished.isFalse()) {
                            sb.append("</pre><pre style='color: red'>Przerwane wywołanie funkcji - być może zapętlenie.</pre><pre><br />");
                        }
                    } catch (InterruptedException e) {
                        sb.append("</pre><pre style='color: red'>Przerwane wywołanie funkcji - być może zapętlenie.</pre><pre><br />");
                    }
                    sb.append(sw.getBuffer().toString());
                    sw.getBuffer().setLength(0);
                    if (goal != null && !goalListeners.isEmpty()) {
                        try {
                            PyObject res = interpreter.eval(goal);
                            if (res instanceof PyBoolean) {
                                PyBoolean bool = (PyBoolean) res;
                                if (bool.getBooleanValue()) {
                                    for (GoalListener listener : goalListeners) {
                                        listener.goalMatched();
                                    }
                                }
                            }
                        } catch (PyException e) {
                            // ignore
                        }
                    }
                } else {
                    sb.append("</pre><pre style='color: red'>Niepoprawna składnia. Sprawdź, czy wszystkie cudzysłowy i nawiasy są poprawnie zamknięte.</pre><pre><br />");
                }
            } catch (PyException e) {
                sb.append("</pre><pre style='color: red'>").append(ErrorMessageTranslator.translateError(StringEscapeUtils.escapeHtml4(e.value.toString()))).append("</pre><pre><br />");
                sw.getBuffer().setLength(0);
            }
        }
	}
}
