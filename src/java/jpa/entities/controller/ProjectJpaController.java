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
import jpa.entities.Billable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import jpa.entities.Project;
import jpa.entities.ProjectPK;
import jpa.entities.controller.exceptions.IllegalOrphanException;
import jpa.entities.controller.exceptions.NonexistentEntityException;
import jpa.entities.controller.exceptions.PreexistingEntityException;

/**
 *
 * @author vanessa.costa
 */
public class ProjectJpaController implements Serializable {

    public ProjectJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Project project) throws PreexistingEntityException, Exception {
        if (project.getProjectPK() == null) {
            project.setProjectPK(new ProjectPK());
        }
        if (project.getBillableCollection() == null) {
            project.setBillableCollection(new ArrayList<Billable>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<Billable> attachedBillableCollection = new ArrayList<Billable>();
            for (Billable billableCollectionBillableToAttach : project.getBillableCollection()) {
                billableCollectionBillableToAttach = em.getReference(billableCollectionBillableToAttach.getClass(), billableCollectionBillableToAttach.getBillableId());
                attachedBillableCollection.add(billableCollectionBillableToAttach);
            }
            project.setBillableCollection(attachedBillableCollection);
            em.persist(project);
            for (Billable billableCollectionBillable : project.getBillableCollection()) {
                Project oldProjectOfBillableCollectionBillable = billableCollectionBillable.getProject();
                billableCollectionBillable.setProject(project);
                billableCollectionBillable = em.merge(billableCollectionBillable);
                if (oldProjectOfBillableCollectionBillable != null) {
                    oldProjectOfBillableCollectionBillable.getBillableCollection().remove(billableCollectionBillable);
                    oldProjectOfBillableCollectionBillable = em.merge(oldProjectOfBillableCollectionBillable);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findProject(project.getProjectPK()) != null) {
                throw new PreexistingEntityException("Project " + project + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Project project) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Project persistentProject = em.find(Project.class, project.getProjectPK());
            Collection<Billable> billableCollectionOld = persistentProject.getBillableCollection();
            Collection<Billable> billableCollectionNew = project.getBillableCollection();
            List<String> illegalOrphanMessages = null;
            for (Billable billableCollectionOldBillable : billableCollectionOld) {
                if (!billableCollectionNew.contains(billableCollectionOldBillable)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Billable " + billableCollectionOldBillable + " since its project field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Collection<Billable> attachedBillableCollectionNew = new ArrayList<Billable>();
            for (Billable billableCollectionNewBillableToAttach : billableCollectionNew) {
                billableCollectionNewBillableToAttach = em.getReference(billableCollectionNewBillableToAttach.getClass(), billableCollectionNewBillableToAttach.getBillableId());
                attachedBillableCollectionNew.add(billableCollectionNewBillableToAttach);
            }
            billableCollectionNew = attachedBillableCollectionNew;
            project.setBillableCollection(billableCollectionNew);
            project = em.merge(project);
            for (Billable billableCollectionNewBillable : billableCollectionNew) {
                if (!billableCollectionOld.contains(billableCollectionNewBillable)) {
                    Project oldProjectOfBillableCollectionNewBillable = billableCollectionNewBillable.getProject();
                    billableCollectionNewBillable.setProject(project);
                    billableCollectionNewBillable = em.merge(billableCollectionNewBillable);
                    if (oldProjectOfBillableCollectionNewBillable != null && !oldProjectOfBillableCollectionNewBillable.equals(project)) {
                        oldProjectOfBillableCollectionNewBillable.getBillableCollection().remove(billableCollectionNewBillable);
                        oldProjectOfBillableCollectionNewBillable = em.merge(oldProjectOfBillableCollectionNewBillable);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                ProjectPK id = project.getProjectPK();
                if (findProject(id) == null) {
                    throw new NonexistentEntityException("The project with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(ProjectPK id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Project project;
            try {
                project = em.getReference(Project.class, id);
                project.getProjectPK();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The project with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Billable> billableCollectionOrphanCheck = project.getBillableCollection();
            for (Billable billableCollectionOrphanCheckBillable : billableCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Project (" + project + ") cannot be destroyed since the Billable " + billableCollectionOrphanCheckBillable + " in its billableCollection field has a non-nullable project field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(project);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Project> findProjectEntities() {
        return findProjectEntities(true, -1, -1);
    }

    public List<Project> findProjectEntities(int maxResults, int firstResult) {
        return findProjectEntities(false, maxResults, firstResult);
    }

    private List<Project> findProjectEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Project.class));
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

    public Project findProject(ProjectPK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Project.class, id);
        } finally {
            em.close();
        }
    }

    public int getProjectCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Project> rt = cq.from(Project.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
