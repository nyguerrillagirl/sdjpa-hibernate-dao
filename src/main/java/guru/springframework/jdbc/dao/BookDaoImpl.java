package guru.springframework.jdbc.dao;

import guru.springframework.jdbc.domain.Book;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Component;

@Component
public class BookDaoImpl implements BookDao {
    private final EntityManagerFactory emf;

    public BookDaoImpl(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public Book getById(Long id) {
        EntityManager em = getEntityManager();
        try {
            Book book =   em.find(Book.class, id);
            return book;
        } finally {
            em.close();
        }
     }

    @Override
    public Book findBookByTitle(String title) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Book> query = em
                    .createQuery("SELECT b FROM Book b WHERE b.title = :title", Book.class);
            query.setParameter("title", title);
            Book book = query.getSingleResult();
            return book;
        } finally {
            em.close();
        }
    }

    @Override
    public Book saveNewBook(Book book) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(book);
            em.flush(); // forces Hibernate to write to the database
            em.getTransaction().commit();
            return book;
        } finally {
            em.close();
        }
    }

    @Override
    public Book updateBook(Book book) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(book);
            em.flush();
            em.clear();
            Book updatedBook = em.find(Book.class, book.getId());
            em.getTransaction().commit();
            return updatedBook;
        } finally {
            em.close();
        }
    }

    @Override
    public void deleteBookById(Long id) {
        EntityManager em = getEntityManager();
        em.getTransaction().begin();
        Book book = em.find(Book.class, id);
        em.remove(book);
        em.flush();
        em.getTransaction().commit();
        em.close();
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

}