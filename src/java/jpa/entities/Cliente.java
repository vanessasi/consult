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
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
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
@Table(name = "cliente")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Cliente.findAll", query = "SELECT c FROM Cliente c"),
    @NamedQuery(name = "Cliente.findByClientName", query = "SELECT c FROM Cliente c WHERE c.clientePK.clientName = :clientName"),
    @NamedQuery(name = "Cliente.findByClientDepartmentNumber", query = "SELECT c FROM Cliente c WHERE c.clientePK.clientDepartmentNumber = :clientDepartmentNumber"),
    @NamedQuery(name = "Cliente.findByContactEmail", query = "SELECT c FROM Cliente c WHERE c.contactEmail = :contactEmail"),
    @NamedQuery(name = "Cliente.findByContactPassword", query = "SELECT c FROM Cliente c WHERE c.contactPassword = :contactPassword")})
public class Cliente implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected ClientePK clientePK;
    @Basic(optional = false)
    @Column(name = "CONTACT_EMAIL")
    private String contactEmail;
    @Column(name = "CONTACT_PASSWORD")
    private String contactPassword;
    @JoinColumn(name = "BILLING_ADDRESS", referencedColumnName = "ADDRESS_ID")
    @ManyToOne
    private Address billingAddress;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "cliente")
    private Collection<Recruiter> recruiterCollection;

    public Cliente() {
    }

    public Cliente(ClientePK clientePK) {
        this.clientePK = clientePK;
    }

    public Cliente(ClientePK clientePK, String contactEmail) {
        this.clientePK = clientePK;
        this.contactEmail = contactEmail;
    }

    public Cliente(String clientName, short clientDepartmentNumber) {
        this.clientePK = new ClientePK(clientName, clientDepartmentNumber);
    }

    public ClientePK getClientePK() {
        return clientePK;
    }

    public void setClientePK(ClientePK clientePK) {
        this.clientePK = clientePK;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getContactPassword() {
        return contactPassword;
    }

    public void setContactPassword(String contactPassword) {
        this.contactPassword = contactPassword;
    }

    public Address getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(Address billingAddress) {
        this.billingAddress = billingAddress;
    }

    @XmlTransient
    public Collection<Recruiter> getRecruiterCollection() {
        return recruiterCollection;
    }

    public void setRecruiterCollection(Collection<Recruiter> recruiterCollection) {
        this.recruiterCollection = recruiterCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (clientePK != null ? clientePK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Cliente)) {
            return false;
        }
        Cliente other = (Cliente) object;
        if ((this.clientePK == null && other.clientePK != null) || (this.clientePK != null && !this.clientePK.equals(other.clientePK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "jpa.entities.Cliente[ clientePK=" + clientePK + " ]";
    }
    
}
