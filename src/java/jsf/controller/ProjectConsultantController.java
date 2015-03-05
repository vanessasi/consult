package jsf.controller;

import jpa.entities.ProjectConsultant;
import jsf.controller.util.JsfUtil;
import jsf.controller.util.PaginationHelper;
import jpa.entities.controller.ProjectConsultantJpaController;

import java.io.Serializable;
import java.util.ResourceBundle;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import javax.persistence.Persistence;

@ManagedBean(name = "projectConsultantController")
@SessionScoped
public class ProjectConsultantController implements Serializable {

    private ProjectConsultant current;
    private DataModel items = null;
    private ProjectConsultantJpaController jpaController = null;
    private PaginationHelper pagination;
    private int selectedItemIndex;

    public ProjectConsultantController() {
    }

    public ProjectConsultant getSelected() {
        if (current == null) {
            current = new ProjectConsultant();
            current.setProjectConsultantPK(new jpa.entities.ProjectConsultantPK());
            selectedItemIndex = -1;
        }
        return current;
    }

    private ProjectConsultantJpaController getJpaController() {
        if (jpaController == null) {
            jpaController = new ProjectConsultantJpaController(Persistence.createEntityManagerFactory("consultPU"));
        }
        return jpaController;
    }

    public PaginationHelper getPagination() {
        if (pagination == null) {
            pagination = new PaginationHelper(10) {
                @Override
                public int getItemsCount() {
                    return getJpaController().getProjectConsultantCount();
                }

                @Override
                public DataModel createPageDataModel() {
                    return new ListDataModel(getJpaController().findProjectConsultantEntities(getPageSize(), getPageFirstItem()));
                }
            };
        }
        return pagination;
    }

    public String prepareList() {
        recreateModel();
        return "List";
    }

    public String prepareView() {
        current = (ProjectConsultant) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "View";
    }

    public String prepareCreate() {
        current = new ProjectConsultant();
        current.setProjectConsultantPK(new jpa.entities.ProjectConsultantPK());
        selectedItemIndex = -1;
        return "Create";
    }

    public String create() {
        try {
            getJpaController().create(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("ProjectConsultantCreated"));
            return prepareCreate();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String prepareEdit() {
        current = (ProjectConsultant) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "Edit";
    }

    public String update() {
        try {
            getJpaController().edit(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("ProjectConsultantUpdated"));
            return "View";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String destroy() {
        current = (ProjectConsultant) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        performDestroy();
        recreatePagination();
        recreateModel();
        return "List";
    }

    public String destroyAndView() {
        performDestroy();
        recreateModel();
        updateCurrentItem();
        if (selectedItemIndex >= 0) {
            return "View";
        } else {
            // all items were removed - go back to list
            recreateModel();
            return "List";
        }
    }

    private void performDestroy() {
        try {
            getJpaController().destroy(current.getProjectConsultantPK());
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("ProjectConsultantDeleted"));
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        }
    }

    private void updateCurrentItem() {
        int count = getJpaController().getProjectConsultantCount();
        if (selectedItemIndex >= count) {
            // selected index cannot be bigger than number of items:
            selectedItemIndex = count - 1;
            // go to previous page if last page disappeared:
            if (pagination.getPageFirstItem() >= count) {
                pagination.previousPage();
            }
        }
        if (selectedItemIndex >= 0) {
            current = getJpaController().findProjectConsultantEntities(1, selectedItemIndex).get(0);
        }
    }

    public DataModel getItems() {
        if (items == null) {
            items = getPagination().createPageDataModel();
        }
        return items;
    }

    private void recreateModel() {
        items = null;
    }

    private void recreatePagination() {
        pagination = null;
    }

    public String next() {
        getPagination().nextPage();
        recreateModel();
        return "List";
    }

    public String previous() {
        getPagination().previousPage();
        recreateModel();
        return "List";
    }

    public SelectItem[] getItemsAvailableSelectMany() {
        return JsfUtil.getSelectItems(getJpaController().findProjectConsultantEntities(), false);
    }

    public SelectItem[] getItemsAvailableSelectOne() {
        return JsfUtil.getSelectItems(getJpaController().findProjectConsultantEntities(), true);
    }

    @FacesConverter(forClass = ProjectConsultant.class)
    public static class ProjectConsultantControllerConverter implements Converter {

        private static final String SEPARATOR = "#";
        private static final String SEPARATOR_ESCAPED = "\\#";

        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            ProjectConsultantController controller = (ProjectConsultantController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "projectConsultantController");
            return controller.getJpaController().findProjectConsultant(getKey(value));
        }

        jpa.entities.ProjectConsultantPK getKey(String value) {
            jpa.entities.ProjectConsultantPK key;
            String values[] = value.split(SEPARATOR_ESCAPED);
            key = new jpa.entities.ProjectConsultantPK();
            key.setClientNameProjCons(values[0]);
            key.setClientDepartmentNumberProjCons(Short.parseShort(values[1]));
            key.setProjectNameCons(values[2]);
            key.setConsultantIdProj(Integer.parseInt(values[3]));
            return key;
        }

        String getStringKey(jpa.entities.ProjectConsultantPK value) {
            StringBuffer sb = new StringBuffer();
            sb.append(value.getClientNameProjCons());
            sb.append(SEPARATOR);
            sb.append(value.getClientDepartmentNumberProjCons());
            sb.append(SEPARATOR);
            sb.append(value.getProjectNameCons());
            sb.append(SEPARATOR);
            sb.append(value.getConsultantIdProj());
            return sb.toString();
        }

        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof ProjectConsultant) {
                ProjectConsultant o = (ProjectConsultant) object;
                return getStringKey(o.getProjectConsultantPK());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + ProjectConsultant.class.getName());
            }
        }
    }
}
