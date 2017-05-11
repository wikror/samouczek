package pl.pwl.samouczek.python;

import java.util.List;

import pl.pwl.samouczek.orm.jpa.MaterialEntity;
import pl.pwl.samouczek.python.PythonInterpreterWindow.GoalListener;
import pl.pwl.samouczek.ui.SamouczekUtil;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.VerticalLayout;

public class StagedLesson extends VerticalLayout implements GoalListener {

	private static final long serialVersionUID = 1L;

	public static class Stage {
		
		private String goal;
		private String htmlDescription;
		
		public String getGoal() {
			return goal;
		}
		public void setGoal(String goal) {
			this.goal = goal;
		}
		public String getHtmlDescription() {
			return htmlDescription;
		}
		public void setHtmlDescription(String htmlDescription) {
			this.htmlDescription = htmlDescription;
		}
	
	}
	
	private List<Stage> stages;
	int currentStage;
	
	private VerticalLayout instructions;
	private PythonInterpreterWindow interpreter;
	private MaterialEntity material;
	
	public StagedLesson(MaterialEntity material, List<Stage> stages) {
		this.stages = stages;
		this.currentStage = 0;
		this.material = material;
		init();
	}

	private void init() {
		instructions = new VerticalLayout();
		Label instructionLabel = new Label();
		instructionLabel.setContentMode(ContentMode.HTML);
		Stage stage = stages.get(currentStage);
		instructionLabel.setValue(stage.getHtmlDescription());
		instructions.addComponent(instructionLabel);
		interpreter = new PythonInterpreterWindow();
		interpreter.setGoal(stage.getGoal());
		interpreter.registerGoalListener(this);
				
		addComponent(instructions);
		addComponent(interpreter);
		
		setExpandRatio(instructions, 0.5f);
		setExpandRatio(interpreter, 1.0f);
		
		setHeight("100%");
	}

	@Override
	public void goalMatched() {
		currentStage++;
		if (currentStage >= stages.size()) {
			Notification.show("Gratulacje, udało Ci się wykonać całe zadanie!", Type.WARNING_MESSAGE);
			if (material != null) {
				SamouczekUtil.markMaterialAsRealized(material);
			}
			interpreter.setGoal(null);
			instructions.removeAllComponents();
			Label instructionLabel = new Label();
			instructionLabel.setContentMode(ContentMode.HTML);
			instructionLabel.setValue("Gratulacje, udało Ci się wykonać całe zadanie!");
			instructions.addComponent(instructionLabel);
		} else {
			Stage stage = stages.get(currentStage);
			instructions.removeAllComponents();
			Label instructionLabel = new Label();
			instructionLabel.setContentMode(ContentMode.HTML);
			instructionLabel.setValue(stage.getHtmlDescription());
			instructions.addComponent(instructionLabel);
			interpreter.setGoal(stage.getGoal());
		}
	}
	
}
