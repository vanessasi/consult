/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jpa.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author vanessa.costa
 */
@Entity
@Table(name = "billable")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Billable.findAll", query = "SELECT b FROM Billable b"),
    @NamedQuery(name = "Billable.findByBillableId", query = "SELECT b FROM Billable b WHERE b.billableId = :billableId"),
    @NamedQuery(name = "Billable.findByStartDate", query = "SELECT b FROM Billable b WHERE b.startDate = :startDate"),
    @NamedQuery(name = "Billable.findByEndDate", query = "SELECT b FROM Billable b WHERE b.endDate = :endDate"),
    @NamedQuery(name = "Billable.findByHours", query = "SELECT b FROM Billable b WHERE b.hours = :hours"),
    @NamedQuery(name = "Billable.findByHourlyRate", query = "SELECT b FROM Billable b WHERE b.hourlyRate = :hourlyRate"),
    @NamedQuery(name = "Billable.findByBillableHourlyRate", query = "SELECT b FROM Billable b WHERE b.billableHourlyRate = :billableHourlyRate"),
    @NamedQuery(name = "Billable.findByDescription", query = "SELECT b FROM Billable b WHERE b.description = :description")})
public class Billable implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "BILLABLE_ID")
    private Long billableId;
    @Basic(optional = false)
    @Column(name = "START_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startDate;
    @Basic(optional = false)
    @Column(name = "END_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date endDate;
    @Column(name = "HOURS")
    private Short hours;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "HOURLY_RATE")
    private BigDecimal hourlyRate;
    @Column(name = "BILLABLE_HOURLY_RATE")
    private BigDecimal billableHourlyRate;
    @Column(name = "DESCRIPTION")
    private String description;
    @Lob
    @Column(name = "ARTIFACTS")
    private String artifacts;
    @JoinColumns({
        @JoinColumn(name = "CLIENT_NAME_BI", referencedColumnName = "CLIENT_NAME_PROJ"),
        @JoinColumn(name = "CLIENT_DEPARTMENT_NUMBER", referencedColumnName = "CLIENT_DEPARTMENT_NUMBER_PROJ"),
        @JoinColumn(name = "PROJECT_NAME_BI", referencedColumnName = "PROJECT_NAME_PROJ")})
    @ManyToOne(optional = false)
    private Project project;
    @JoinColumn(name = "CONSULTANT_ID_BI", referencedColumnName = "CONSULTANT_ID")
    @ManyToOne(optional = false)
    private Consultant consultantIdBi;

    public Billable() {
    }

    public Billable(Long billableId) {
        this.billableId = billableId;
    }

    public Billable(Long billableId, Date startDate, Date endDate) {
        this.billableId = billableId;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Long getBillableId() {
        return billableId;
    }

    public void setBillableId(Long billableId) {
        this.billableId = billableId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Short getHours() {
        return hours;
    }

    public void setHours(Short hours) {
        this.hours = hours;
    }

    public BigDecimal getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(BigDecimal hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    public BigDecimal getBillableHourlyRate() {
        return billableHourlyRate;
    }

    public void setBillableHourlyRate(BigDecimal billableHourlyRate) {
        this.billableHourlyRate = billableHourlyRate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getArtifacts() {
        return artifacts;
    }

    public void setArtifacts(String artifacts) {
        this.artifacts = artifacts;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Consultant getConsultantIdBi() {
        return consultantIdBi;
    }

    public void setConsultantIdBi(Consultant consultantIdBi) {
        this.consultantIdBi = consultantIdBi;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (billableId != null ? billableId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Billable)) {
            return false;
        }
        Billable other = (Billable) object;
        if ((this.billableId == null && other.billableId != null) || (this.billableId != null && !this.billableId.equals(other.billableId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "jpa.entities.Billable[ billableId=" + billableId + " ]";
    }
    
}
