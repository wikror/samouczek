package pl.pwl.samouczek.sorting;

import pl.pwl.samouczek.orm.jpa.MaterialEntity;
import pl.pwl.samouczek.ui.SamouczekUtil;

import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptAll;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.ui.AbstractComponentContainer;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.DragAndDropWrapper;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.VerticalLayout;

public class SortingGrid extends GridLayout {

	private static final long serialVersionUID = 1L;
	
	private int countTiles = 0;
	
	private class CellDropHandler implements DropHandler {

		private static final long serialVersionUID = 1L;
		private int col;
		private int row;

		public CellDropHandler(int col, int row) {
			this.col = col;
			this.row = row;
		}
		
		@Override
		public void drop(DragAndDropEvent event) {
			Component comp = event.getTransferable().getSourceComponent();
			if (comp != null && comp.getParent() instanceof AbstractComponentContainer) {
				AbstractComponentContainer previousParent = ((AbstractComponentContainer) comp.getParent()); 
				VerticalLayout dropLayout = dropWrappers[col][row].getLayout();
				previousParent.removeComponent(comp);
				if (dropLayout.getComponentCount() > 0) {
					Component previousComponent = dropLayout.getComponent(0);
					dropLayout.removeComponent(previousComponent);
					previousParent.addComponent(previousComponent);
				}
				dropLayout.addComponent(comp);
			}
			checkSorted();
		}

		@Override
		public AcceptCriterion getAcceptCriterion() {
			return AcceptAll.get();
		}
		
	}
	
	public static class DroppableCell extends DragAndDropWrapper {

		private static final long serialVersionUID = 1L;
		private VerticalLayout layout = new VerticalLayout();

		public DroppableCell(DropHandler dropHandler) {
			super(null);
			setSizeFull();
			layout.setSizeFull();
			layout.addStyleName("sorting-grid-cell");
			setCompositionRoot(layout);
			setDropHandler(dropHandler);
		}
		
		public VerticalLayout getLayout() {
			return layout;
		}
		
	}
	
	public static class Tile extends DragAndDropWrapper {
		
		private static final long serialVersionUID = 1L;
		private Object value;

		public Tile(Object labelContent) {
			super(null);
			setSizeFull();
			VerticalLayout layout = new VerticalLayout();
			layout.setSizeFull();
			Label label = new Label(String.valueOf(labelContent));
			layout.addStyleName("sorting-tile");
			label.addStyleName("sorting-tile-content");
			layout.addComponent(label);
			layout.setComponentAlignment(label, Alignment.MIDDLE_CENTER);
			setCompositionRoot(layout);
			this.value = labelContent;
			setDragStartMode(DragStartMode.COMPONENT);
		}
		
		public Object getTileValue() {
			return value;
		}
		
	}
	
	private DroppableCell dropWrappers[][];
	private MaterialEntity material;

	private int cols;
	@SuppressWarnings("unused")
	private int rows;

	public SortingGrid(int cols, int rows) {
		super(cols, rows);
		this.cols = cols;
		this.rows = rows;
		setSizeFull();
		setHideEmptyRowsAndColumns(false);
		//addStyleName("sorting-grid");
		dropWrappers = new DroppableCell[cols][rows];
		for (int i = 0; i < cols; i++) {
			setColumnExpandRatio(i, 1.0f);
			for (int j = 0; j < rows; j++) {
				setRowExpandRatio(j, 1.0f);
				dropWrappers[i][j] = new DroppableCell(new CellDropHandler(i, j));
				addComponent(dropWrappers[i][j], i, j);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public void checkSorted() {
		boolean sorted = true;
		Comparable<Object> previous = null;
		for (int i = 0; i < countTiles; i++) {
			int row = (i - (i % cols)) / cols;
			int col = i % cols;
			VerticalLayout layout = dropWrappers[col][row].getLayout();
			if (layout.getComponentCount() == 0) {
				sorted = false; 
				break;
			}
			Component comp = layout.getComponent(0);
			if (!(comp instanceof Tile)) {
				sorted = false;
				break;
			}
			Tile tile = (Tile) comp;
			Comparable<Object> current = (Comparable<Object>) tile.getTileValue();
			if (previous != null && previous.compareTo(current) >= 0) {
				sorted = false;
				break;
			}
			previous = current;
		}
		if (sorted) {
			Notification.show("Gratulacje, udało Ci się posortować wszystkie elementy!", Type.WARNING_MESSAGE);
			if (material != null) {
				SamouczekUtil.markMaterialAsRealized(material);
			}
		}
	}

	public void insertTile(int col, int row, Object value) {
		Tile tile = new Tile(value);
		dropWrappers[col][row].getLayout().addComponent(tile);
		countTiles++;
	}
	
	public void linkWithMaterial(MaterialEntity material) {
		this.material = material;
	}
	
	public DroppableCell getCell(int col, int row) {
		return dropWrappers[col][row];
	}
}
