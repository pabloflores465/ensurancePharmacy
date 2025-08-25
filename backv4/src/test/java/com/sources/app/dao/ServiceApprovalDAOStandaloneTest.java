package com.sources.app.dao;

import com.sources.app.entities.ServiceApproval;
import com.sources.app.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ServiceApprovalDAOStandaloneTest {

    @Mock private SessionFactory mockSessionFactory;
    @Mock private Session mockSession;
    @Mock private Transaction mockTransaction;
    @Mock private Query<ServiceApproval> mockApprovalQuery;
    @Mock private Query<ServiceApproval> mockListQuery;

    private ServiceApprovalDAO dao;

    @BeforeEach
    void setUp() {
        dao = new ServiceApprovalDAO();
    }

    @AfterEach
    void tearDown() {
        // reset injected SessionFactory to avoid cross-test leakage
        HibernateUtil.setSessionFactory(null);
    }

    @Test
    void create_GeneratesCodeAndDate_AndCommits() {
        // inject mocked SessionFactory into HibernateUtil singleton for this test
        HibernateUtil.setSessionFactory(mockSessionFactory);
        when(mockSessionFactory.openSession()).thenReturn(mockSession);
        when(mockSession.beginTransaction()).thenReturn(mockTransaction);
        ServiceApproval input = new ServiceApproval();
        input.setUserId(1L);
        input.setHospitalId(2L);
        input.setServiceId("SVC-1");
        input.setServiceName("XRay");
        input.setServiceCost(100.0);
        input.setCoveredAmount(80.0);
        input.setPatientAmount(20.0);
        input.setStatus("PENDING");

        ServiceApproval saved = dao.create(input);
        assertNotNull(saved);
        assertNotNull(saved.getApprovalCode());
        assertTrue(saved.getApprovalCode().startsWith("AP"));
        assertEquals(10, saved.getApprovalCode().length());
        assertNotNull(saved.getApprovalDate());
        assertTrue(saved.getApprovalDate() instanceof Date);

        verify(mockSession).save(any(ServiceApproval.class));
        verify(mockTransaction).commit();
    }

    @Test
    void create_OnException_RollsBackAndReturnsNull() {
        HibernateUtil.setSessionFactory(mockSessionFactory);
        when(mockSessionFactory.openSession()).thenReturn(mockSession);
        when(mockSession.beginTransaction()).thenReturn(mockTransaction);
        doThrow(new RuntimeException("fail")).when(mockSession).save(any());

        ServiceApproval input = new ServiceApproval();
        input.setUserId(1L);
        input.setHospitalId(2L);
        input.setServiceId("SVC-1");
        input.setServiceName("XRay");
        input.setServiceCost(100.0);
        input.setCoveredAmount(80.0);
        input.setPatientAmount(20.0);
        input.setStatus("PENDING");

        ServiceApproval result = dao.create(input);
        assertNull(result);
        verify(mockTransaction).rollback();
    }

    @Test
    void update_CommitsAndReturns() {
        HibernateUtil.setSessionFactory(mockSessionFactory);
        when(mockSessionFactory.openSession()).thenReturn(mockSession);
        when(mockSession.beginTransaction()).thenReturn(mockTransaction);
        ServiceApproval approval = new ServiceApproval();
        approval.setId(10L);
        approval.setApprovalCode("AP123456");

        ServiceApproval updated = dao.update(approval);
        assertNotNull(updated);
        verify(mockSession).update(approval);
        verify(mockTransaction).commit();
    }

    @Test
    void getById_UsesSessionGet() {
        HibernateUtil.setSessionFactory(mockSessionFactory);
        when(mockSessionFactory.openSession()).thenReturn(mockSession);
        ServiceApproval entity = new ServiceApproval();
        entity.setId(5L);
        when(mockSession.get(eq(ServiceApproval.class), eq(5L))).thenReturn(entity);

        ServiceApproval found = dao.getById(5L);
        assertNotNull(found);
        assertEquals(5L, found.getId());
    }

    @Test
    void getByApprovalCode_UsesQueryUniqueResult() {
        HibernateUtil.setSessionFactory(mockSessionFactory);
        when(mockSessionFactory.openSession()).thenReturn(mockSession);
        ServiceApproval entity = new ServiceApproval();
        entity.setApprovalCode("APCODE");

        when(mockSession.createQuery(anyString(), eq(ServiceApproval.class))).thenReturn(mockApprovalQuery);
        when(mockApprovalQuery.setParameter(eq("code"), any())).thenReturn(mockApprovalQuery);
        when(mockApprovalQuery.uniqueResult()).thenReturn(entity);

        ServiceApproval found = dao.getByApprovalCode("APCODE");
        assertNotNull(found);
        assertEquals("APCODE", found.getApprovalCode());
    }

    @Test
    void getByUserId_ReturnsList() {
        HibernateUtil.setSessionFactory(mockSessionFactory);
        when(mockSessionFactory.openSession()).thenReturn(mockSession);
        ServiceApproval a = new ServiceApproval(); a.setUserId(7L);
        when(mockSession.createQuery(anyString(), eq(ServiceApproval.class))).thenReturn(mockListQuery);
        when(mockListQuery.setParameter(eq("userId"), any())).thenReturn(mockListQuery);
        when(mockListQuery.list()).thenReturn(List.of(a));

        List<ServiceApproval> list = dao.getByUserId(7L);
        assertNotNull(list);
        assertEquals(1, list.size());
        assertEquals(7L, list.get(0).getUserId());
    }

    @Test
    void updatePrescriptionInfo_Found_DelegatesToUpdate() {
        ServiceApprovalDAO spyDao = Mockito.spy(new ServiceApprovalDAO());
        ServiceApproval existing = new ServiceApproval();
        existing.setApprovalCode("APZZZZZZ");

        doReturn(existing).when(spyDao).getByApprovalCode("APZZZZZZ");
        doReturn(existing).when(spyDao).update(any(ServiceApproval.class));

        ServiceApproval result = spyDao.updatePrescriptionInfo("APZZZZZZ", 42L, 123.45);
        assertNotNull(result);
        assertEquals(42L, result.getPrescriptionId());
        assertEquals(123.45, result.getPrescriptionTotal());
        verify(spyDao).update(any(ServiceApproval.class));
    }

    @Test
    void updateStatus_Rejected_SetsReason() {
        ServiceApprovalDAO spyDao = Mockito.spy(new ServiceApprovalDAO());
        ServiceApproval existing = new ServiceApproval();
        existing.setApprovalCode("APPPPPPP");

        doReturn(existing).when(spyDao).getByApprovalCode("APPPPPPP");
        doReturn(existing).when(spyDao).update(any(ServiceApproval.class));

        ServiceApproval result = spyDao.updateStatus("APPPPPPP", "REJECTED", "Bad docs");
        assertNotNull(result);
        assertEquals("REJECTED", result.getStatus());
        assertEquals("Bad docs", result.getRejectionReason());
        verify(spyDao).update(any(ServiceApproval.class));
    }
}
