package pl.pwl.samouczek.sorting;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import pl.pwl.samouczek.material.InteractiveMaterial;
import pl.pwl.samouczek.orm.jpa.MaterialEntity;

import com.vaadin.ui.Component;

public class SortingGridMaterial implements InteractiveMaterial {

	public static enum SortingGridValueType {
		numbers
	}

	private SortingGrid grid;
	
	@Override
	public void init(MaterialEntity material, String configString) {
		int rows = 20, cols = 30, min = 1, max = 10, amount = 10;
		SortingGridValueType type = SortingGridValueType.numbers;
		if (configString != null) {
			String[] splitParams = configString.split("&");
			if (splitParams != null) {
				for (String split : splitParams) {
					String[] nameAndValue = split.split("=");
					if (nameAndValue.length == 2) {
						String name = nameAndValue[0];
						String value = nameAndValue[1];
						if ("height".equals(name)) {
							rows = Integer.parseInt(value);
						} else if ("width".equals(name)) {
							cols = Integer.parseInt(value);
						} else if ("min".equals(name)) {
							min = Integer.parseInt(value);
						} else if ("max".equals(name)) {
							max = Integer.parseInt(value);
						} else if ("amount".equals(name)) {
							amount = Integer.parseInt(value);
						} else if ("type".equals(name)) {
							type = SortingGridValueType.valueOf(value);
						}
					}
				}
			}
			this.grid = new SortingGrid(cols, rows);
			grid.linkWithMaterial(material);
		}
		switch (type) {
		case numbers:
			Set<Integer> generatedNumbers = new HashSet<>();
			Set<Integer> occupiedLocations = new HashSet<>();
			if (amount > max - min + 1) {
				throw new IllegalArgumentException("Too many numbers to generate.");
			}
			if (amount > rows * cols) {
				throw new IllegalArgumentException("Grid too small for this amount.");
			}
			for (int i = 0; i < amount; i++) {
				Integer number = new Random().nextInt(max - min + 1) + min;
				while (generatedNumbers.contains(number)) {
					number = new Random().nextInt(max - min + 1) + min;
				}
				Integer position = new Random().nextInt(rows * cols);
				while (occupiedLocations.contains(position)) {
					position = new Random().nextInt(rows * cols);
				}
				occupiedLocations.add(position);
				generatedNumbers.add(number);
				int col = position % cols;
				int row = (position - (position % cols)) / cols;
				grid.insertTile(col, row, number);
			}
			break;
		default:
			break;
		}
	}

	@Override
	public Component getLessonComponent() {
		return grid;
	}

}
