package pl.pwl.samouczek.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pl.pwl.samouczek.orm.jpa.LessonEntity;
import pl.pwl.samouczek.orm.jpa.MaterialEntity;
import pl.pwl.samouczek.orm.jpa.UserEntity;
import pl.pwl.samouczek.persistence.PersistenceConfig;
import pl.pwl.samouczek.persistence.commons.jpa.JpaEnvironments;
import pl.pwl.samouczek.renderer.MaterialRenderer;
import pl.pwl.samouczek.security.AuthenticationService;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.BaseTheme;

public class MainWindow extends VerticalLayout {

	private static final long serialVersionUID = 1L;
	//private SortingGrid sortingGrid;
		
	private HorizontalLayout topMenu;
	private HorizontalLayout contentsLayout;
	
	private VerticalLayout contentsMenu;
	private VerticalLayout contentContent;
	private Accordion lessonsAccordion;
	
	public MainWindow() {
		addStyleName("samouczek-main");
		setSizeFull();
		
		topMenu = new HorizontalLayout();
		topMenu.addStyleName("samouczek-top");
		topMenu.setHeight("100px");
		topMenu.setWidth("100%");
		
		addComponent(topMenu);
		
		contentsLayout = new HorizontalLayout();
		contentsLayout.addStyleName("samouczek-contents");
		contentsLayout.setSizeFull();
				
		contentsMenu = new VerticalLayout();
		contentsMenu.addStyleName("samouczek-contentlist");
		contentsMenu.setWidth("300px");
		contentsMenu.setHeight(null);
		
		contentContent = new VerticalLayout();
		contentContent.addStyleName("samouczek-lesson");
		contentContent.setSizeFull();
		
		addComponent(contentsLayout);
		
		contentsLayout.addComponent(contentsMenu);
		contentsLayout.addComponent(contentContent);
		
		contentsLayout.setExpandRatio(contentsMenu, 0.0f);
		contentsLayout.setExpandRatio(contentContent, 1.0f);
		
		setExpandRatio(topMenu, 0.0f);
		setExpandRatio(contentsLayout, 1.0f);
		
		initTop();
		initContents();
		
		/*super(2, 2);
		this.sortingGrid = new SortingGrid(10, 5);
		addComponent(sortingGrid, 0, 0, 1, 1);
		Label label = new Label("1");
		DragAndDropWrapper wrapper = new DragAndDropWrapper(label);
		wrapper.setDragStartMode(DragStartMode.COMPONENT);
		sortingGrid.getCell(0, 0).getLayout().addComponent(wrapper);
		sortingGrid.getCell(0, 0).getLayout().setComponentAlignment(wrapper, Alignment.MIDDLE_CENTER);
		
		Label label2 = new Label("2");
		DragAndDropWrapper wrapper2 = new DragAndDropWrapper(label2);
		wrapper2.setDragStartMode(DragStartMode.COMPONENT);
		sortingGrid.getCell(0, 1).getLayout().addComponent(wrapper2);
		sortingGrid.getCell(0, 1).getLayout().setComponentAlignment(wrapper2, Alignment.MIDDLE_CENTER);*/
		
		
	}

	public void initContents() {
		initMenu();
		contentContent.removeAllComponents();
	}

	public void initMenu() {
		Integer selTab = lessonsAccordion == null ? null : lessonsAccordion.getTabPosition(lessonsAccordion.getTab(lessonsAccordion.getSelectedTab()));
		contentsMenu.removeAllComponents();
		
		Map<LessonEntity, List<MaterialEntity>> userMaterialsByLesson = new HashMap<>();
		UserEntity loginInfo = AuthenticationService.getCurrentLoginInfo();
		if (loginInfo != null) {
			loadUserMaterials(userMaterialsByLesson, loginInfo);
		}
		renderUserMaterials(userMaterialsByLesson);
		if (selTab != null) {
			lessonsAccordion.setSelectedTab(selTab);
		}
	}

	protected void loadUserMaterials(Map<LessonEntity, List<MaterialEntity>> userMaterialsByLesson, UserEntity loginInfo) {
		Set<MaterialEntity> allUserMaterials = loginInfo.getMaterials();
		for (MaterialEntity mat : allUserMaterials) {
			if (!userMaterialsByLesson.containsKey(mat.getLesson())) {
				userMaterialsByLesson.put(mat.getLesson(), new ArrayList<>());
			}
			userMaterialsByLesson.get(mat.getLesson()).add(mat);
		}
		for (List<MaterialEntity> lst : userMaterialsByLesson.values()) {
			Collections.sort(lst, (mat1, mat2) ->  {
				return new Integer(mat1.getId()).compareTo(new Integer(mat2.getId()));
			});
		}
	}

	private void renderUserMaterials(Map<LessonEntity, List<MaterialEntity>> userMaterialsByLesson) {
		this.lessonsAccordion = new Accordion();
		List<LessonEntity> lessons = new ArrayList<>(userMaterialsByLesson.keySet());
		Collections.sort(lessons, (l1, l2) -> {
			return l1.getId().compareTo(l2.getId());
		});
		int i = 0;
		for (LessonEntity les : lessons) {
			List<MaterialEntity> materials = userMaterialsByLesson.get(les);
			VerticalLayout vl = new VerticalLayout();
			lessonsAccordion.addTab(vl, les.getTitle());
			
			for (MaterialEntity material : materials) {
				Button materialButton = new Button(material.getTitle());
				materialButton.addStyleName(BaseTheme.BUTTON_LINK);
				materialButton.addClickListener(event -> {
					contentContent.removeAllComponents();
					contentContent.addComponent(MaterialRenderer.renderMaterial(material));
				});
				vl.addComponent(materialButton);
				vl.setComponentAlignment(materialButton, Alignment.TOP_LEFT);
			}
			i++;
		}
		lessonsAccordion.setSelectedTab(i - 1);
		contentsMenu.addComponent(lessonsAccordion);
	}

	public void initTop() {
		topMenu.removeAllComponents();
		UserEntity loginInfo = AuthenticationService.getCurrentLoginInfo();
		
		if (loginInfo != null) {
			String text = "Zalogowany jako <b>" + loginInfo.getFullname() + "</b> (rola: " + loginInfo.getRole() + ")";
			Label loginLabel = new Label(text, ContentMode.HTML);
						
			Button logoutButton = new Button("Wyloguj");
			
			topMenu.addComponent(loginLabel);
			topMenu.setComponentAlignment(loginLabel, Alignment.TOP_LEFT);
			
			if (AuthenticationService.isAdmin(loginInfo)) {
				Button reloadButton = new Button("Przeładuj kontekst bazodanowy");
				reloadButton.addClickListener((click) -> {
					JpaEnvironments.getInstance().getJpaEnvironment(PersistenceConfig.JPA_PERSISTENCE_UNIT_NAME).reloadContext();
					AuthenticationService.reloadUserInfo();
					initContents();
				});
				topMenu.addComponent(reloadButton);
				topMenu.setComponentAlignment(reloadButton, Alignment.TOP_RIGHT);
			}
			
			topMenu.addComponent(logoutButton);
			topMenu.setComponentAlignment(logoutButton, Alignment.TOP_RIGHT);
			
			logoutButton.addClickListener(event -> {
				AuthenticationService.logout();
				UI.getCurrent().setContent(new LoginWindow());
			});
		}
	}

}
 