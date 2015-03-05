/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jpa.entities;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
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
@Table(name = "recruiter")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Recruiter.findAll", query = "SELECT r FROM Recruiter r"),
    @NamedQuery(name = "Recruiter.findByRecruiterId", query = "SELECT r FROM Recruiter r WHERE r.recruiterId = :recruiterId"),
    @NamedQuery(name = "Recruiter.findByEmail", query = "SELECT r FROM Recruiter r WHERE r.email = :email"),
    @NamedQuery(name = "Recruiter.findByPasswordRe", query = "SELECT r FROM Recruiter r WHERE r.passwordRe = :passwordRe")})
public class Recruiter implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "RECRUITER_ID")
    private Integer recruiterId;
    @Basic(optional = false)
    @Column(name = "EMAIL")
    private String email;
    @Basic(optional = false)
    @Column(name = "PASSWORD_RE")
    private String passwordRe;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "recruiterIdCons")
    private Collection<Consultant> consultantCollection;
    @JoinColumns({
        @JoinColumn(name = "CLIENT_NAME", referencedColumnName = "CLIENT_NAME"),
        @JoinColumn(name = "CLIENT_DEPARTMENT_NUMBER", referencedColumnName = "CLIENT_DEPARTMENT_NUMBER")})
    @ManyToOne(optional = false)
    private Cliente cliente;

    public Recruiter() {
    }

    public Recruiter(Integer recruiterId) {
        this.recruiterId = recruiterId;
    }

    public Recruiter(Integer recruiterId, String email, String passwordRe) {
        this.recruiterId = recruiterId;
        this.email = email;
        this.passwordRe = passwordRe;
    }

    public Integer getRecruiterId() {
        return recruiterId;
    }

    public void setRecruiterId(Integer recruiterId) {
        this.recruiterId = recruiterId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordRe() {
        return passwordRe;
    }

    public void setPasswordRe(String passwordRe) {
        this.passwordRe = passwordRe;
    }

    @XmlTransient
    public Collection<Consultant> getConsultantCollection() {
        return consultantCollection;
    }

    public void setConsultantCollection(Collection<Consultant> consultantCollection) {
        this.consultantCollection = consultantCollection;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (recruiterId != null ? recruiterId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Recruiter)) {
            return false;
        }
        Recruiter other = (Recruiter) object;
        if ((this.recruiterId == null && other.recruiterId != null) || (this.recruiterId != null && !this.recruiterId.equals(other.recruiterId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "jpa.entities.Recruiter[ recruiterId=" + recruiterId + " ]";
    }
    
}
