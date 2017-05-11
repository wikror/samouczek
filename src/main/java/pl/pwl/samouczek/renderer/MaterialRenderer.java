package pl.pwl.samouczek.renderer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import pl.pwl.samouczek.material.InteractiveMaterial;
import pl.pwl.samouczek.orm.jpa.MaterialEntity;
import pl.pwl.samouczek.python.StagedLesson;
import pl.pwl.samouczek.python.StagedLesson.Stage;
import pl.pwl.samouczek.ui.SamouczekUtil;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class MaterialRenderer {

	@SuppressWarnings("unchecked")
	public static Component renderMaterial(MaterialEntity material) {
		if ("text".equals(material.getType())) {
			VerticalLayout layout = new VerticalLayout();
			layout.addComponent(new Label(material.getContents()));
			SamouczekUtil.markMaterialAsRealized(material);
			return layout;
		} else if ("html".equals(material.getType())) {
			VerticalLayout layout = new VerticalLayout();
			layout.addComponent(new Label(material.getContents(), ContentMode.HTML));
			SamouczekUtil.markMaterialAsRealized(material);
			return layout;
		} else if ("component".equals(material.getType())) {
			String configStr = material.getContents();
			String[] spl = configStr.split("#");
			if (spl == null || spl.length == 0) {
				throw new IllegalArgumentException();
			}
			String className = spl[0];
			try {
				Class<? extends InteractiveMaterial> materialGenerator = (Class<? extends InteractiveMaterial>) Class.forName(className);
				InteractiveMaterial materialComponent = materialGenerator.newInstance();
				String configParams = null;
				if (spl.length > 1) {
					configParams = spl[1];
				}
				materialComponent.init(material, configParams);
				return materialComponent.getLessonComponent();
			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		} else if ("staged".equals(material.getType())) {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			try {
				dbf.setNamespaceAware(false);
				dbf.setValidating(false);
				DocumentBuilder db = dbf.newDocumentBuilder();
				ByteArrayInputStream bais = new ByteArrayInputStream(material.getContents().getBytes("UTF-8"));
				Document dom = db.parse(bais);
				Element mainElement = dom.getDocumentElement();
				NodeList stages = mainElement.getElementsByTagName("stage");
				List<Stage> stagesList = new ArrayList<>();
				for (int i = 0; i < stages.getLength(); i++) {
					Element currentStage = (Element) stages.item(i);
					Document descriptionDocument = DocumentBuilderFactory.newInstance()
			                .newDocumentBuilder().newDocument();
					Element descriptionElement = (Element) currentStage.getElementsByTagName("description").item(0);
					Element clonedDescription = (Element) descriptionElement.cloneNode(true);
					descriptionDocument.adoptNode(clonedDescription);
					descriptionDocument.appendChild(clonedDescription);
					Element goalElement = (Element) currentStage.getElementsByTagName("goal").item(0);
					String goal = goalElement.getTextContent();
					String description = XmlUtils.prettyPrintedDocument(descriptionDocument);
					Stage stage = new Stage();
					stage.setGoal(goal);
					stage.setHtmlDescription(description);
					stagesList.add(stage);
				}
				StagedLesson lesson = new StagedLesson(material, stagesList);
				return lesson;
			} catch (ParserConfigurationException | SAXException | IOException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}

		}
		return new Label("Not supported yet.");
	}
	
}
