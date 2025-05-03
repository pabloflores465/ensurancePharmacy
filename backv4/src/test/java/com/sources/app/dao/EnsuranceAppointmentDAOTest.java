package com.sources.app.dao;

import com.sources.app.entities.EnsuranceAppointment;
import com.sources.app.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import jakarta.persistence.NoResultException;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnsuranceAppointmentDAOTest {

    @Mock
    private SessionFactory mockSessionFactory;
    @Mock
    private Session mockSession;
    @Mock
    private Transaction mockTransaction;
    @Mock
    private Query<EnsuranceAppointment> mockQuery;

    private MockedStatic<HibernateUtil> mockedHibernateUtil;

    @InjectMocks
    private EnsuranceAppointmentDAO ensuranceAppointmentDAO;

    @BeforeEach
    void setUp() {
        mockedHibernateUtil = Mockito.mockStatic(HibernateUtil.class);
        mockedHibernateUtil.when(HibernateUtil::getSessionFactory).thenReturn(mockSessionFactory);
        lenient().when(mockSessionFactory.openSession()).thenReturn(mockSession);
        lenient().when(mockSession.beginTransaction()).thenReturn(mockTransaction);
        lenient().when(mockSession.createQuery(anyString(), eq(EnsuranceAppointment.class))).thenReturn(mockQuery);
    }

    @AfterEach
    void tearDown() {
        mockedHibernateUtil.close();
    }

    @Test
    void create_Success() {
        // Arrange
        String hospitalAppointmentId = "HOSP123";
        Long userId = 1L;
        Date appDate = new Date();
        String doctor = "Dr. Smith";
        String reason = "Checkup";

        // Act
        EnsuranceAppointment result = ensuranceAppointmentDAO.create(hospitalAppointmentId, userId, appDate, doctor, reason);

        // Assert
        assertNotNull(result);
        assertEquals(hospitalAppointmentId, result.getHospitalAppointmentId());
        assertEquals(userId, result.getIdUser());
        assertEquals(appDate, result.getAppointmentDate());
        assertEquals(doctor, result.getDoctorName());
        assertEquals(reason, result.getReason());
        verify(mockSession).save(any(EnsuranceAppointment.class));
        verify(mockTransaction).commit();
    }

    @Test
    void create_Exception() {
        // Arrange
        String hospitalAppId = "HOSP123";
        Long userId = 1L;
        Date date = new Date();
        String doctor = "Dr. Error";
        String reason = "Test Exception";
        
        doThrow(new RuntimeException("DB Save Error")).when(mockSession).save(any(EnsuranceAppointment.class));

        // Act
        EnsuranceAppointment result = ensuranceAppointmentDAO.create(hospitalAppId, userId, date, doctor, reason);

        // Assert: Expect the non-null object returned by DAO despite save failure
        assertNotNull(result);
        // Check if fields were set correctly before potential save failure
        assertEquals(hospitalAppId, result.getHospitalAppointmentId());
        assertEquals(userId, result.getIdUser());
        assertEquals(date, result.getAppointmentDate());
        assertEquals(doctor, result.getDoctorName());
        assertEquals(reason, result.getReason());
        
        verify(mockSession).save(any(EnsuranceAppointment.class));
        verify(mockTransaction).rollback();
        verify(mockTransaction, never()).commit();
    }

    @Test
    void findById_Found() {
        // Arrange
        Long id = 1L;
        EnsuranceAppointment expectedApp = new EnsuranceAppointment();
        when(mockSession.get(EnsuranceAppointment.class, id)).thenReturn(expectedApp);

        // Act
        EnsuranceAppointment result = ensuranceAppointmentDAO.findById(id);

        // Assert
        assertNotNull(result);
        assertEquals(expectedApp, result);
        verify(mockSession).get(EnsuranceAppointment.class, id);
    }

    @Test
    void findById_NotFound() {
        // Arrange
        Long id = 1L;
        when(mockSession.get(EnsuranceAppointment.class, id)).thenReturn(null);

        // Act
        EnsuranceAppointment result = ensuranceAppointmentDAO.findById(id);

        // Assert
        assertNull(result);
        verify(mockSession).get(EnsuranceAppointment.class, id);
    }

    @Test
    void findById_Exception() {
        // Arrange
        Long id = 1L;
        when(mockSession.get(EnsuranceAppointment.class, id)).thenThrow(new RuntimeException("DB Error"));

        // Act
        EnsuranceAppointment result = ensuranceAppointmentDAO.findById(id);

        // Assert
        assertNull(result);
        verify(mockSession).get(EnsuranceAppointment.class, id);
    }
    
    @Test
    void findByHospitalAppointmentId_Found() {
        // Arrange
        String hospId = "HOSP456";
        EnsuranceAppointment expectedApp = new EnsuranceAppointment();
        when(mockQuery.setParameter(eq("hospitalId"), eq(hospId))).thenReturn(mockQuery);
        when(mockQuery.uniqueResult()).thenReturn(expectedApp);

        // Act
        EnsuranceAppointment result = ensuranceAppointmentDAO.findByHospitalAppointmentId(hospId);

        // Assert
        assertNotNull(result);
        assertEquals(expectedApp, result);
        verify(mockSession).createQuery("FROM EnsuranceAppointment WHERE hospitalAppointmentId = :hospitalId", EnsuranceAppointment.class);
        verify(mockQuery).setParameter("hospitalId", hospId);
    }

    @Test
    void findByHospitalAppointmentId_NotFound() {
        // Arrange
        String hospitalId = "99";
        String hql = "FROM EnsuranceAppointment WHERE hospitalAppointmentId = :hospitalId";
        when(mockSession.createQuery(eq(hql), eq(EnsuranceAppointment.class))).thenReturn(mockQuery);
        when(mockQuery.setParameter(eq("hospitalId"), eq(hospitalId))).thenReturn(mockQuery);
        when(mockQuery.uniqueResult()).thenReturn(null);

        // Act
        EnsuranceAppointment result = ensuranceAppointmentDAO.findByHospitalAppointmentId(hospitalId);

        // Assert
        assertNull(result, "Result should be null when appointment is not found");
        verify(mockSession).createQuery(eq(hql), eq(EnsuranceAppointment.class));
        verify(mockQuery).setParameter("hospitalId", hospitalId);
        verify(mockQuery).uniqueResult();
        verify(mockTransaction, never()).rollback();
    }

    @Test
    void findByHospitalAppointmentId_Exception() {
        // Arrange
        String hospitalId = "1";
        String hql = "FROM EnsuranceAppointment WHERE hospitalAppointmentId = :hospitalId";
        when(mockSession.createQuery(eq(hql), eq(EnsuranceAppointment.class))).thenThrow(new RuntimeException("DB Error"));

        // Act
        EnsuranceAppointment result = ensuranceAppointmentDAO.findByHospitalAppointmentId(hospitalId);

        // Assert
        assertNull(result);
        verify(mockSession).createQuery(eq(hql), eq(EnsuranceAppointment.class));
        verify(mockQuery, never()).setParameter(anyString(), any());
        verify(mockQuery, never()).getSingleResult();
        verify(mockQuery, never()).uniqueResult();
    }

    @Test
    void findByUserId_Success() {
        // Arrange
        Long userId = 1L;
        List<EnsuranceAppointment> expectedList = Collections.singletonList(new EnsuranceAppointment());
        when(mockQuery.setParameter(eq("userId"), eq(userId))).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(expectedList);

        // Act
        List<EnsuranceAppointment> result = ensuranceAppointmentDAO.findByUserId(userId);

        // Assert
        assertNotNull(result);
        assertEquals(expectedList, result);
        verify(mockSession).createQuery("FROM EnsuranceAppointment WHERE idUser = :userId ORDER BY appointmentDate DESC", EnsuranceAppointment.class);
        verify(mockQuery).setParameter("userId", userId);
    }
    
    @Test
    void findByUserId_Exception() {
        // Arrange
        Long userId = 1L;
        String hql = "FROM EnsuranceAppointment WHERE idUser = :userId ORDER BY appointmentDate DESC";
        when(mockSession.createQuery(eq(hql), eq(EnsuranceAppointment.class)))
            .thenThrow(new RuntimeException("DB Error"));

        // Act
        List<EnsuranceAppointment> result = ensuranceAppointmentDAO.findByUserId(userId);

        // Assert
        assertNull(result);
        verify(mockSession).createQuery(eq(hql), eq(EnsuranceAppointment.class));
        verify(mockQuery, never()).setParameter(anyString(), any());
        verify(mockQuery, never()).getResultList();
    }

    @Test
    void findAll_Success() {
        // Arrange
        List<EnsuranceAppointment> expectedList = Collections.singletonList(new EnsuranceAppointment());
        when(mockQuery.getResultList()).thenReturn(expectedList);

        // Act
        List<EnsuranceAppointment> result = ensuranceAppointmentDAO.findAll();

        // Assert
        assertNotNull(result);
        assertEquals(expectedList, result);
        verify(mockSession).createQuery("FROM EnsuranceAppointment ORDER BY appointmentDate DESC", EnsuranceAppointment.class);
    }
    
     @Test
    void findAll_Exception() {
        // Arrange
        when(mockSession.createQuery(anyString(), eq(EnsuranceAppointment.class))).thenThrow(new RuntimeException("DB Error"));

        // Act
        List<EnsuranceAppointment> result = ensuranceAppointmentDAO.findAll();

        // Assert
        assertNull(result);
        verify(mockSession).createQuery(anyString(), eq(EnsuranceAppointment.class));
        verify(mockQuery, never()).getResultList();
    }

    @Test
    void update_Success() {
        // Arrange
        EnsuranceAppointment appToUpdate = new EnsuranceAppointment();

        // Act
        EnsuranceAppointment result = ensuranceAppointmentDAO.update(appToUpdate);

        // Assert
        assertNotNull(result);
        assertEquals(appToUpdate, result);
        verify(mockSession).update(appToUpdate);
        verify(mockTransaction).commit();
    }

    @Test
    void update_Exception() {
        // Arrange
        EnsuranceAppointment appToUpdate = new EnsuranceAppointment();
        doThrow(new RuntimeException("DB Update Error")).when(mockSession).update(any(EnsuranceAppointment.class));

        // Act
        EnsuranceAppointment result = ensuranceAppointmentDAO.update(appToUpdate);

        // Assert
        assertNull(result);
        verify(mockSession).update(appToUpdate);
        verify(mockTransaction).rollback();
        verify(mockTransaction, never()).commit();
    }

    @Test
    void delete_Success() {
        // Arrange
        Long id = 1L;
        EnsuranceAppointment appToDelete = new EnsuranceAppointment();
        when(mockSession.get(EnsuranceAppointment.class, id)).thenReturn(appToDelete);

        // Act
        boolean result = ensuranceAppointmentDAO.delete(id);

        // Assert
        assertTrue(result);
        verify(mockSession).get(EnsuranceAppointment.class, id);
        verify(mockSession).delete(appToDelete);
        verify(mockTransaction).commit();
    }

    @Test
    void delete_NotFound() {
        // Arrange
        Long id = 1L;
        when(mockSession.get(EnsuranceAppointment.class, id)).thenReturn(null);

        // Act
        boolean result = ensuranceAppointmentDAO.delete(id);

        // Assert
        assertFalse(result);
        verify(mockSession).get(EnsuranceAppointment.class, id);
        verify(mockSession, never()).delete(any());
        verify(mockTransaction, never()).commit();
    }

    @Test
    void delete_Exception() {
        // Arrange
        Long id = 1L;
        EnsuranceAppointment appToDelete = new EnsuranceAppointment();
        when(mockSession.get(EnsuranceAppointment.class, id)).thenReturn(appToDelete);
        doThrow(new RuntimeException("DB Delete Error")).when(mockSession).delete(any(EnsuranceAppointment.class));

        // Act
        boolean result = ensuranceAppointmentDAO.delete(id);

        // Assert
        assertFalse(result);
        verify(mockSession).delete(appToDelete);
        verify(mockTransaction).rollback();
        verify(mockTransaction, never()).commit();
    }
    
     @Test
    void deleteByHospitalAppointmentId_Success() {
        // Arrange
        String hospId = "HOSP789";
        EnsuranceAppointment appToDelete = new EnsuranceAppointment();
        when(mockQuery.setParameter(eq("hospitalId"), eq(hospId))).thenReturn(mockQuery);
        when(mockQuery.uniqueResult()).thenReturn(appToDelete);

        // Act
        boolean result = ensuranceAppointmentDAO.deleteByHospitalAppointmentId(hospId);

        // Assert
        assertTrue(result);
        verify(mockSession).createQuery("FROM EnsuranceAppointment WHERE hospitalAppointmentId = :hospitalId", EnsuranceAppointment.class);
        verify(mockQuery).setParameter("hospitalId", hospId);
        verify(mockSession).delete(appToDelete);
        verify(mockTransaction).commit();
    }

    @Test
    void deleteByHospitalAppointmentId_NotFound() {
        // Arrange
        String hospId = "HOSP789";
        when(mockQuery.setParameter(eq("hospitalId"), eq(hospId))).thenReturn(mockQuery);
        when(mockQuery.uniqueResult()).thenReturn(null);

        // Act
        boolean result = ensuranceAppointmentDAO.deleteByHospitalAppointmentId(hospId);

        // Assert
        assertFalse(result);
        verify(mockQuery).uniqueResult();
        verify(mockSession, never()).delete(any());
        verify(mockTransaction, never()).commit();
    }

    @Test
    void deleteByHospitalAppointmentId_Exception() {
        // Arrange
        String hospId = "HOSP789";
        EnsuranceAppointment appToDelete = new EnsuranceAppointment();
        when(mockQuery.setParameter(eq("hospitalId"), eq(hospId))).thenReturn(mockQuery);
        when(mockQuery.uniqueResult()).thenReturn(appToDelete);
        doThrow(new RuntimeException("DB Delete Error")).when(mockSession).delete(any(EnsuranceAppointment.class));

        // Act
        boolean result = ensuranceAppointmentDAO.deleteByHospitalAppointmentId(hospId);

        // Assert
        assertFalse(result);
        verify(mockSession).delete(appToDelete);
        verify(mockTransaction).rollback();
        verify(mockTransaction, never()).commit();
    }
    
    @Test
    void findByDate_Success() {
        // Arrange
        Date searchDate = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(searchDate);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date startDate = cal.getTime(); // Start of the day

        cal.add(Calendar.DATE, 1);
        Date endDate = cal.getTime(); // Start of the next day

        String expectedHql = "FROM EnsuranceAppointment WHERE appointmentDate BETWEEN :startDate AND :endDate";
        List<EnsuranceAppointment> expectedList = Collections.singletonList(new EnsuranceAppointment());

        when(mockSession.createQuery(eq(expectedHql), eq(EnsuranceAppointment.class))).thenReturn(mockQuery);
        // Use any(Date.class) for parameter matching
        when(mockQuery.setParameter(eq("startDate"), any(Date.class))).thenReturn(mockQuery);
        when(mockQuery.setParameter(eq("endDate"), any(Date.class))).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(expectedList);

        // Act
        List<EnsuranceAppointment> result = ensuranceAppointmentDAO.findByDate(searchDate);

        // Assert
        assertNotNull(result, "Result should not be null");
        assertEquals(expectedList, result);
        verify(mockSession).createQuery(eq(expectedHql), eq(EnsuranceAppointment.class));
        // Verify using any(Date.class) as well
        verify(mockQuery).setParameter(eq("startDate"), any(Date.class));
        verify(mockQuery).setParameter(eq("endDate"), any(Date.class));
        verify(mockQuery).getResultList();
    }
    
     @Test
    void findByDate_Exception() {
        // Arrange
        Date searchDate = new Date();
        when(mockSession.createQuery(anyString(), eq(EnsuranceAppointment.class))).thenThrow(new RuntimeException("DB Error"));

        // Act
        List<EnsuranceAppointment> result = ensuranceAppointmentDAO.findByDate(searchDate);

        // Assert
        assertNull(result);
        verify(mockSession).createQuery(anyString(), eq(EnsuranceAppointment.class));
        verify(mockQuery, never()).setParameter(anyString(), any());
        verify(mockQuery, never()).getResultList();
    }
    
    // findTodayAppointments just calls findByDate, so testing findByDate thoroughly covers it.
    // Optionally add a simple test to ensure findByDate is called.
     @Test
    void findTodayAppointments_CallsFindByDate() {
        // Arrange
        EnsuranceAppointmentDAO daoSpy = Mockito.spy(ensuranceAppointmentDAO); // Spy to verify method call
        List<EnsuranceAppointment> expectedList = Collections.emptyList();
        // Must stub the spied method to avoid real execution
        doReturn(expectedList).when(daoSpy).findByDate(any(Date.class)); 

        // Act
        List<EnsuranceAppointment> result = daoSpy.findTodayAppointments();

        // Assert
        assertEquals(expectedList, result);
        verify(daoSpy).findByDate(any(Date.class)); // Verify findByDate was called
    }
} 