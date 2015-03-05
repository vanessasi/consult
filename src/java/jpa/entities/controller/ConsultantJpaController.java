/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jpa.entities.controller;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import jpa.entities.ConsultantStatus;
import jpa.entities.Recruiter;
import jpa.entities.ProjectConsultant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import jpa.entities.Billable;
import jpa.entities.Consultant;
import jpa.entities.controller.exceptions.IllegalOrphanException;
import jpa.entities.controller.exceptions.NonexistentEntityException;
import jpa.entities.controller.exceptions.PreexistingEntityException;

/**
 *
 * @author vanessa.costa
 */
public class ConsultantJpaController implements Serializable {

    public ConsultantJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Consultant consultant) throws PreexistingEntityException, Exception {
        if (consultant.getProjectConsultantCollection() == null) {
            consultant.setProjectConsultantCollection(new ArrayList<ProjectConsultant>());
        }
        if (consultant.getBillableCollection() == null) {
            consultant.setBillableCollection(new ArrayList<Billable>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            ConsultantStatus statusId = consultant.getStatusId();
            if (statusId != null) {
                statusId = em.getReference(statusId.getClass(), statusId.getStatusId());
                consultant.setStatusId(statusId);
            }
            Recruiter recruiterIdCons = consultant.getRecruiterIdCons();
            if (recruiterIdCons != null) {
                recruiterIdCons = em.getReference(recruiterIdCons.getClass(), recruiterIdCons.getRecruiterId());
                consultant.setRecruiterIdCons(recruiterIdCons);
            }
            Collection<ProjectConsultant> attachedProjectConsultantCollection = new ArrayList<ProjectConsultant>();
            for (ProjectConsultant projectConsultantCollectionProjectConsultantToAttach : consultant.getProjectConsultantCollection()) {
                projectConsultantCollectionProjectConsultantToAttach = em.getReference(projectConsultantCollectionProjectConsultantToAttach.getClass(), projectConsultantCollectionProjectConsultantToAttach.getProjectConsultantPK());
                attachedProjectConsultantCollection.add(projectConsultantCollectionProjectConsultantToAttach);
            }
            consultant.setProjectConsultantCollection(attachedProjectConsultantCollection);
            Collection<Billable> attachedBillableCollection = new ArrayList<Billable>();
            for (Billable billableCollectionBillableToAttach : consultant.getBillableCollection()) {
                billableCollectionBillableToAttach = em.getReference(billableCollectionBillableToAttach.getClass(), billableCollectionBillableToAttach.getBillableId());
                attachedBillableCollection.add(billableCollectionBillableToAttach);
            }
            consultant.setBillableCollection(attachedBillableCollection);
            em.persist(consultant);
            if (statusId != null) {
                statusId.getConsultantCollection().add(consultant);
                statusId = em.merge(statusId);
            }
            if (recruiterIdCons != null) {
                recruiterIdCons.getConsultantCollection().add(consultant);
                recruiterIdCons = em.merge(recruiterIdCons);
            }
            for (ProjectConsultant projectConsultantCollectionProjectConsultant : consultant.getProjectConsultantCollection()) {
                Consultant oldConsultantOfProjectConsultantCollectionProjectConsultant = projectConsultantCollectionProjectConsultant.getConsultant();
                projectConsultantCollectionProjectConsultant.setConsultant(consultant);
                projectConsultantCollectionProjectConsultant = em.merge(projectConsultantCollectionProjectConsultant);
                if (oldConsultantOfProjectConsultantCollectionProjectConsultant != null) {
                    oldConsultantOfProjectConsultantCollectionProjectConsultant.getProjectConsultantCollection().remove(projectConsultantCollectionProjectConsultant);
                    oldConsultantOfProjectConsultantCollectionProjectConsultant = em.merge(oldConsultantOfProjectConsultantCollectionProjectConsultant);
                }
            }
            for (Billable billableCollectionBillable : consultant.getBillableCollection()) {
                Consultant oldConsultantIdBiOfBillableCollectionBillable = billableCollectionBillable.getConsultantIdBi();
                billableCollectionBillable.setConsultantIdBi(consultant);
                billableCollectionBillable = em.merge(billableCollectionBillable);
                if (oldConsultantIdBiOfBillableCollectionBillable != null) {
                    oldConsultantIdBiOfBillableCollectionBillable.getBillableCollection().remove(billableCollectionBillable);
                    oldConsultantIdBiOfBillableCollectionBillable = em.merge(oldConsultantIdBiOfBillableCollectionBillable);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findConsultant(consultant.getConsultantId()) != null) {
                throw new PreexistingEntityException("Consultant " + consultant + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Consultant consultant) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Consultant persistentConsultant = em.find(Consultant.class, consultant.getConsultantId());
            ConsultantStatus statusIdOld = persistentConsultant.getStatusId();
            ConsultantStatus statusIdNew = consultant.getStatusId();
            Recruiter recruiterIdConsOld = persistentConsultant.getRecruiterIdCons();
            Recruiter recruiterIdConsNew = consultant.getRecruiterIdCons();
            Collection<ProjectConsultant> projectConsultantCollectionOld = persistentConsultant.getProjectConsultantCollection();
            Collection<ProjectConsultant> projectConsultantCollectionNew = consultant.getProjectConsultantCollection();
            Collection<Billable> billableCollectionOld = persistentConsultant.getBillableCollection();
            Collection<Billable> billableCollectionNew = consultant.getBillableCollection();
            List<String> illegalOrphanMessages = null;
            for (ProjectConsultant projectConsultantCollectionOldProjectConsultant : projectConsultantCollectionOld) {
                if (!projectConsultantCollectionNew.contains(projectConsultantCollectionOldProjectConsultant)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain ProjectConsultant " + projectConsultantCollectionOldProjectConsultant + " since its consultant field is not nullable.");
                }
            }
            for (Billable billableCollectionOldBillable : billableCollectionOld) {
                if (!billableCollectionNew.contains(billableCollectionOldBillable)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Billable " + billableCollectionOldBillable + " since its consultantIdBi field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (statusIdNew != null) {
                statusIdNew = em.getReference(statusIdNew.getClass(), statusIdNew.getStatusId());
                consultant.setStatusId(statusIdNew);
            }
            if (recruiterIdConsNew != null) {
                recruiterIdConsNew = em.getReference(recruiterIdConsNew.getClass(), recruiterIdConsNew.getRecruiterId());
                consultant.setRecruiterIdCons(recruiterIdConsNew);
            }
            Collection<ProjectConsultant> attachedProjectConsultantCollectionNew = new ArrayList<ProjectConsultant>();
            for (ProjectConsultant projectConsultantCollectionNewProjectConsultantToAttach : projectConsultantCollectionNew) {
                projectConsultantCollectionNewProjectConsultantToAttach = em.getReference(projectConsultantCollectionNewProjectConsultantToAttach.getClass(), projectConsultantCollectionNewProjectConsultantToAttach.getProjectConsultantPK());
                attachedProjectConsultantCollectionNew.add(projectConsultantCollectionNewProjectConsultantToAttach);
            }
            projectConsultantCollectionNew = attachedProjectConsultantCollectionNew;
            consultant.setProjectConsultantCollection(projectConsultantCollectionNew);
            Collection<Billable> attachedBillableCollectionNew = new ArrayList<Billable>();
            for (Billable billableCollectionNewBillableToAttach : billableCollectionNew) {
                billableCollectionNewBillableToAttach = em.getReference(billableCollectionNewBillableToAttach.getClass(), billableCollectionNewBillableToAttach.getBillableId());
                attachedBillableCollectionNew.add(billableCollectionNewBillableToAttach);
            }
            billableCollectionNew = attachedBillableCollectionNew;
            consultant.setBillableCollection(billableCollectionNew);
            consultant = em.merge(consultant);
            if (statusIdOld != null && !statusIdOld.equals(statusIdNew)) {
                statusIdOld.getConsultantCollection().remove(consultant);
                statusIdOld = em.merge(statusIdOld);
            }
            if (statusIdNew != null && !statusIdNew.equals(statusIdOld)) {
                statusIdNew.getConsultantCollection().add(consultant);
                statusIdNew = em.merge(statusIdNew);
            }
            if (recruiterIdConsOld != null && !recruiterIdConsOld.equals(recruiterIdConsNew)) {
                recruiterIdConsOld.getConsultantCollection().remove(consultant);
                recruiterIdConsOld = em.merge(recruiterIdConsOld);
            }
            if (recruiterIdConsNew != null && !recruiterIdConsNew.equals(recruiterIdConsOld)) {
                recruiterIdConsNew.getConsultantCollection().add(consultant);
                recruiterIdConsNew = em.merge(recruiterIdConsNew);
            }
            for (ProjectConsultant projectConsultantCollectionNewProjectConsultant : projectConsultantCollectionNew) {
                if (!projectConsultantCollectionOld.contains(projectConsultantCollectionNewProjectConsultant)) {
                    Consultant oldConsultantOfProjectConsultantCollectionNewProjectConsultant = projectConsultantCollectionNewProjectConsultant.getConsultant();
                    projectConsultantCollectionNewProjectConsultant.setConsultant(consultant);
                    projectConsultantCollectionNewProjectConsultant = em.merge(projectConsultantCollectionNewProjectConsultant);
                    if (oldConsultantOfProjectConsultantCollectionNewProjectConsultant != null && !oldConsultantOfProjectConsultantCollectionNewProjectConsultant.equals(consultant)) {
                        oldConsultantOfProjectConsultantCollectionNewProjectConsultant.getProjectConsultantCollection().remove(projectConsultantCollectionNewProjectConsultant);
                        oldConsultantOfProjectConsultantCollectionNewProjectConsultant = em.merge(oldConsultantOfProjectConsultantCollectionNewProjectConsultant);
                    }
                }
            }
            for (Billable billableCollectionNewBillable : billableCollectionNew) {
                if (!billableCollectionOld.contains(billableCollectionNewBillable)) {
                    Consultant oldConsultantIdBiOfBillableCollectionNewBillable = billableCollectionNewBillable.getConsultantIdBi();
                    billableCollectionNewBillable.setConsultantIdBi(consultant);
                    billableCollectionNewBillable = em.merge(billableCollectionNewBillable);
                    if (oldConsultantIdBiOfBillableCollectionNewBillable != null && !oldConsultantIdBiOfBillableCollectionNewBillable.equals(consultant)) {
                        oldConsultantIdBiOfBillableCollectionNewBillable.getBillableCollection().remove(billableCollectionNewBillable);
                        oldConsultantIdBiOfBillableCollectionNewBillable = em.merge(oldConsultantIdBiOfBillableCollectionNewBillable);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = consultant.getConsultantId();
                if (findConsultant(id) == null) {
                    throw new NonexistentEntityException("The consultant with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Consultant consultant;
            try {
                consultant = em.getReference(Consultant.class, id);
                consultant.getConsultantId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The consultant with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<ProjectConsultant> projectConsultantCollectionOrphanCheck = consultant.getProjectConsultantCollection();
            for (ProjectConsultant projectConsultantCollectionOrphanCheckProjectConsultant : projectConsultantCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Consultant (" + consultant + ") cannot be destroyed since the ProjectConsultant " + projectConsultantCollectionOrphanCheckProjectConsultant + " in its projectConsultantCollection field has a non-nullable consultant field.");
            }
            Collection<Billable> billableCollectionOrphanCheck = consultant.getBillableCollection();
            for (Billable billableCollectionOrphanCheckBillable : billableCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Consultant (" + consultant + ") cannot be destroyed since the Billable " + billableCollectionOrphanCheckBillable + " in its billableCollection field has a non-nullable consultantIdBi field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            ConsultantStatus statusId = consultant.getStatusId();
            if (statusId != null) {
                statusId.getConsultantCollection().remove(consultant);
                statusId = em.merge(statusId);
            }
            Recruiter recruiterIdCons = consultant.getRecruiterIdCons();
            if (recruiterIdCons != null) {
                recruiterIdCons.getConsultantCollection().remove(consultant);
                recruiterIdCons = em.merge(recruiterIdCons);
            }
            em.remove(consultant);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Consultant> findConsultantEntities() {
        return findConsultantEntities(true, -1, -1);
    }

    public List<Consultant> findConsultantEntities(int maxResults, int firstResult) {
        return findConsultantEntities(false, maxResults, firstResult);
    }

    private List<Consultant> findConsultantEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Consultant.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Consultant findConsultant(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Consultant.class, id);
        } finally {
            em.close();
        }
    }

    public int getConsultantCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Consultant> rt = cq.from(Consultant.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
