/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jpa.entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 *
 * @author vanessa.costa
 */
@Embeddable
public class ProjectConsultantPK implements Serializable {
    @Basic(optional = false)
    @Column(name = "CLIENT_NAME_PROJ_CONS")
    private String clientNameProjCons;
    @Basic(optional = false)
    @Column(name = "CLIENT_DEPARTMENT_NUMBER_PROJ_CONS")
    private short clientDepartmentNumberProjCons;
    @Basic(optional = false)
    @Column(name = "PROJECT_NAME_CONS")
    private String projectNameCons;
    @Basic(optional = false)
    @Column(name = "CONSULTANT_ID_PROJ")
    private int consultantIdProj;

    public ProjectConsultantPK() {
    }

    public ProjectConsultantPK(String clientNameProjCons, short clientDepartmentNumberProjCons, String projectNameCons, int consultantIdProj) {
        this.clientNameProjCons = clientNameProjCons;
        this.clientDepartmentNumberProjCons = clientDepartmentNumberProjCons;
        this.projectNameCons = projectNameCons;
        this.consultantIdProj = consultantIdProj;
    }

    public String getClientNameProjCons() {
        return clientNameProjCons;
    }

    public void setClientNameProjCons(String clientNameProjCons) {
        this.clientNameProjCons = clientNameProjCons;
    }

    public short getClientDepartmentNumberProjCons() {
        return clientDepartmentNumberProjCons;
    }

    public void setClientDepartmentNumberProjCons(short clientDepartmentNumberProjCons) {
        this.clientDepartmentNumberProjCons = clientDepartmentNumberProjCons;
    }

    public String getProjectNameCons() {
        return projectNameCons;
    }

    public void setProjectNameCons(String projectNameCons) {
        this.projectNameCons = projectNameCons;
    }

    public int getConsultantIdProj() {
        return consultantIdProj;
    }

    public void setConsultantIdProj(int consultantIdProj) {
        this.consultantIdProj = consultantIdProj;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (clientNameProjCons != null ? clientNameProjCons.hashCode() : 0);
        hash += (int) clientDepartmentNumberProjCons;
        hash += (projectNameCons != null ? projectNameCons.hashCode() : 0);
        hash += (int) consultantIdProj;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ProjectConsultantPK)) {
            return false;
        }
        ProjectConsultantPK other = (ProjectConsultantPK) object;
        if ((this.clientNameProjCons == null && other.clientNameProjCons != null) || (this.clientNameProjCons != null && !this.clientNameProjCons.equals(other.clientNameProjCons))) {
            return false;
        }
        if (this.clientDepartmentNumberProjCons != other.clientDepartmentNumberProjCons) {
            return false;
        }
        if ((this.projectNameCons == null && other.projectNameCons != null) || (this.projectNameCons != null && !this.projectNameCons.equals(other.projectNameCons))) {
            return false;
        }
        if (this.consultantIdProj != other.consultantIdProj) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "jpa.entities.ProjectConsultantPK[ clientNameProjCons=" + clientNameProjCons + ", clientDepartmentNumberProjCons=" + clientDepartmentNumberProjCons + ", projectNameCons=" + projectNameCons + ", consultantIdProj=" + consultantIdProj + " ]";
    }
    
}
