/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jpa.entities;

import java.io.Serializable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author vanessa.costa
 */
@Entity
@Table(name = "project_consultant")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ProjectConsultant.findAll", query = "SELECT p FROM ProjectConsultant p"),
    @NamedQuery(name = "ProjectConsultant.findByClientNameProjCons", query = "SELECT p FROM ProjectConsultant p WHERE p.projectConsultantPK.clientNameProjCons = :clientNameProjCons"),
    @NamedQuery(name = "ProjectConsultant.findByClientDepartmentNumberProjCons", query = "SELECT p FROM ProjectConsultant p WHERE p.projectConsultantPK.clientDepartmentNumberProjCons = :clientDepartmentNumberProjCons"),
    @NamedQuery(name = "ProjectConsultant.findByProjectNameCons", query = "SELECT p FROM ProjectConsultant p WHERE p.projectConsultantPK.projectNameCons = :projectNameCons"),
    @NamedQuery(name = "ProjectConsultant.findByConsultantIdProj", query = "SELECT p FROM ProjectConsultant p WHERE p.projectConsultantPK.consultantIdProj = :consultantIdProj")})
public class ProjectConsultant implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected ProjectConsultantPK projectConsultantPK;
    @JoinColumn(name = "CONSULTANT_ID_PROJ", referencedColumnName = "CONSULTANT_ID", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Consultant consultant;

    public ProjectConsultant() {
    }

    public ProjectConsultant(ProjectConsultantPK projectConsultantPK) {
        this.projectConsultantPK = projectConsultantPK;
    }

    public ProjectConsultant(String clientNameProjCons, short clientDepartmentNumberProjCons, String projectNameCons, int consultantIdProj) {
        this.projectConsultantPK = new ProjectConsultantPK(clientNameProjCons, clientDepartmentNumberProjCons, projectNameCons, consultantIdProj);
    }

    public ProjectConsultantPK getProjectConsultantPK() {
        return projectConsultantPK;
    }

    public void setProjectConsultantPK(ProjectConsultantPK projectConsultantPK) {
        this.projectConsultantPK = projectConsultantPK;
    }

    public Consultant getConsultant() {
        return consultant;
    }

    public void setConsultant(Consultant consultant) {
        this.consultant = consultant;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (projectConsultantPK != null ? projectConsultantPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ProjectConsultant)) {
            return false;
        }
        ProjectConsultant other = (ProjectConsultant) object;
        if ((this.projectConsultantPK == null && other.projectConsultantPK != null) || (this.projectConsultantPK != null && !this.projectConsultantPK.equals(other.projectConsultantPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "jpa.entities.ProjectConsultant[ projectConsultantPK=" + projectConsultantPK + " ]";
    }
    
}
