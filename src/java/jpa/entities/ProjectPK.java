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
public class ProjectPK implements Serializable {
    @Basic(optional = false)
    @Column(name = "CLIENT_NAME_PROJ")
    private String clientNameProj;
    @Basic(optional = false)
    @Column(name = "CLIENT_DEPARTMENT_NUMBER_PROJ")
    private short clientDepartmentNumberProj;
    @Basic(optional = false)
    @Column(name = "PROJECT_NAME_PROJ")
    private String projectNameProj;

    public ProjectPK() {
    }

    public ProjectPK(String clientNameProj, short clientDepartmentNumberProj, String projectNameProj) {
        this.clientNameProj = clientNameProj;
        this.clientDepartmentNumberProj = clientDepartmentNumberProj;
        this.projectNameProj = projectNameProj;
    }

    public String getClientNameProj() {
        return clientNameProj;
    }

    public void setClientNameProj(String clientNameProj) {
        this.clientNameProj = clientNameProj;
    }

    public short getClientDepartmentNumberProj() {
        return clientDepartmentNumberProj;
    }

    public void setClientDepartmentNumberProj(short clientDepartmentNumberProj) {
        this.clientDepartmentNumberProj = clientDepartmentNumberProj;
    }

    public String getProjectNameProj() {
        return projectNameProj;
    }

    public void setProjectNameProj(String projectNameProj) {
        this.projectNameProj = projectNameProj;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (clientNameProj != null ? clientNameProj.hashCode() : 0);
        hash += (int) clientDepartmentNumberProj;
        hash += (projectNameProj != null ? projectNameProj.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ProjectPK)) {
            return false;
        }
        ProjectPK other = (ProjectPK) object;
        if ((this.clientNameProj == null && other.clientNameProj != null) || (this.clientNameProj != null && !this.clientNameProj.equals(other.clientNameProj))) {
            return false;
        }
        if (this.clientDepartmentNumberProj != other.clientDepartmentNumberProj) {
            return false;
        }
        if ((this.projectNameProj == null && other.projectNameProj != null) || (this.projectNameProj != null && !this.projectNameProj.equals(other.projectNameProj))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "jpa.entities.ProjectPK[ clientNameProj=" + clientNameProj + ", clientDepartmentNumberProj=" + clientDepartmentNumberProj + ", projectNameProj=" + projectNameProj + " ]";
    }
    
}
