/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jpa.entities;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author vanessa.costa
 */
@Entity
@Table(name = "project")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Project.findAll", query = "SELECT p FROM Project p"),
    @NamedQuery(name = "Project.findByClientNameProj", query = "SELECT p FROM Project p WHERE p.projectPK.clientNameProj = :clientNameProj"),
    @NamedQuery(name = "Project.findByClientDepartmentNumberProj", query = "SELECT p FROM Project p WHERE p.projectPK.clientDepartmentNumberProj = :clientDepartmentNumberProj"),
    @NamedQuery(name = "Project.findByProjectNameProj", query = "SELECT p FROM Project p WHERE p.projectPK.projectNameProj = :projectNameProj"),
    @NamedQuery(name = "Project.findByContactEmailProj", query = "SELECT p FROM Project p WHERE p.contactEmailProj = :contactEmailProj"),
    @NamedQuery(name = "Project.findByContactPasswordProj", query = "SELECT p FROM Project p WHERE p.contactPasswordProj = :contactPasswordProj")})
public class Project implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected ProjectPK projectPK;
    @Column(name = "CONTACT_EMAIL_PROJ")
    private String contactEmailProj;
    @Column(name = "CONTACT_PASSWORD_PROJ")
    private String contactPasswordProj;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "project")
    private Collection<Billable> billableCollection;

    public Project() {
    }

    public Project(ProjectPK projectPK) {
        this.projectPK = projectPK;
    }

    public Project(String clientNameProj, short clientDepartmentNumberProj, String projectNameProj) {
        this.projectPK = new ProjectPK(clientNameProj, clientDepartmentNumberProj, projectNameProj);
    }

    public ProjectPK getProjectPK() {
        return projectPK;
    }

    public void setProjectPK(ProjectPK projectPK) {
        this.projectPK = projectPK;
    }

    public String getContactEmailProj() {
        return contactEmailProj;
    }

    public void setContactEmailProj(String contactEmailProj) {
        this.contactEmailProj = contactEmailProj;
    }

    public String getContactPasswordProj() {
        return contactPasswordProj;
    }

    public void setContactPasswordProj(String contactPasswordProj) {
        this.contactPasswordProj = contactPasswordProj;
    }

    @XmlTransient
    public Collection<Billable> getBillableCollection() {
        return billableCollection;
    }

    public void setBillableCollection(Collection<Billable> billableCollection) {
        this.billableCollection = billableCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (projectPK != null ? projectPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Project)) {
            return false;
        }
        Project other = (Project) object;
        if ((this.projectPK == null && other.projectPK != null) || (this.projectPK != null && !this.projectPK.equals(other.projectPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "jpa.entities.Project[ projectPK=" + projectPK + " ]";
    }
    
}
