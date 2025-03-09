package com.sources.app.dao;

import com.sources.app.entities.Comments;
import com.sources.app.entities.User;
import com.sources.app.entities.Medicine;
import com.sources.app.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class CommentsDAO {

    // CREATE
    public Comments create(User user, Comments prevComment, String commentText, Medicine medicine) {
        Transaction tx = null;
        Comments comments = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            comments = new Comments();
            comments.setUser(user);
            comments.setPrevComment(prevComment);
            comments.setCommentText(commentText);
            comments.setMedicine(medicine);

            session.save(comments);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
        return comments;
    }

    // READ ALL
    public List<Comments> getAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Comments> query = session.createQuery("FROM Comments", Comments.class);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // READ BY ID
    public Comments getById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Comments.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // UPDATE
    public Comments update(Comments comments) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.update(comments);
            tx.commit();
            return comments;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return null;
        }
    }
}
