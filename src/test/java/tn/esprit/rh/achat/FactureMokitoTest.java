package tn.esprit.rh.achat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import tn.esprit.rh.achat.entities.Facture;
import tn.esprit.rh.achat.entities.Fournisseur;
import tn.esprit.rh.achat.entities.Operateur;
import tn.esprit.rh.achat.repositories.*;
import tn.esprit.rh.achat.services.FactureServiceImpl;
import tn.esprit.rh.achat.services.ReglementServiceImpl;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class FactureMokitoTest {

    @InjectMocks
    private FactureServiceImpl factureService;

    @Mock
    private FactureRepository factureRepository;
    @Mock
    private OperateurRepository operateurRepository;
    @Mock
    private DetailFactureRepository detailFactureRepository;
    @Mock
    private FournisseurRepository fournisseurRepository;
    @Mock
    private ProduitRepository produitRepository;
    @Mock
    private ReglementServiceImpl reglementService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testRetrieveAllFactures() {
        List<Facture> mockFactures = Arrays.asList(new Facture(), new Facture());
        when(factureRepository.findAll()).thenReturn(mockFactures);

        List<Facture> factures = factureService.retrieveAllFactures();

        assertEquals(mockFactures.size(), factures.size());
        verify(factureRepository, times(1)).findAll();
    }

    @Test
    public void testAddFacture() {
        Facture mockFacture = new Facture();
        when(factureRepository.save(any(Facture.class))).thenReturn(mockFacture);

        Facture facture = factureService.addFacture(new Facture());

        assertNotNull(facture);
        verify(factureRepository, times(1)).save(any(Facture.class));
    }

    @Test
    public void testCancelFacture() {
        Facture mockFacture = new Facture();
        when(factureRepository.findById(anyLong())).thenReturn(Optional.of(mockFacture));

        factureService.cancelFacture(1L);


        verify(factureRepository, times(1)).findById(anyLong());
        verify(factureRepository, times(1)).save(mockFacture);
        verify(factureRepository, times(1)).updateFacture(anyLong());
    }


    @Test
    public void testRetrieveFacture() {
        Facture mockFacture = new Facture();
        when(factureRepository.findById(anyLong())).thenReturn(Optional.of(mockFacture));

        Facture facture = factureService.retrieveFacture(1L);

        assertNotNull(facture);
        verify(factureRepository, times(1)).findById(anyLong());
    }

    @Test
    public void testGetFacturesByFournisseur() {
        Fournisseur mockFournisseur = new Fournisseur();
        mockFournisseur.setFactures(new HashSet<>(Arrays.asList(new Facture(), new Facture())));
        when(fournisseurRepository.findById(anyLong())).thenReturn(Optional.of(mockFournisseur));

        List<Facture> factures = factureService.getFacturesByFournisseur(1L);

        assertEquals(2, factures.size());
        verify(fournisseurRepository, times(1)).findById(anyLong());
    }




    @Test
    public void testAssignOperateurToFacture() {
        Facture mockFacture = new Facture();
        Operateur mockOperateur = new Operateur();
        mockOperateur.setFactures(new HashSet<>());

        when(factureRepository.findById(anyLong())).thenReturn(Optional.of(mockFacture));
        when(operateurRepository.findById(anyLong())).thenReturn(Optional.of(mockOperateur));

        factureService.assignOperateurToFacture(1L, 2L);

        assertTrue(mockOperateur.getFactures().contains(mockFacture));
        verify(factureRepository, times(1)).findById(anyLong());
        verify(operateurRepository, times(1)).findById(anyLong());
        verify(operateurRepository, times(1)).save(mockOperateur);
    }

    @Test
    public void testPourcentageRecouvrement() {
        Date startDate = new Date();
        Date endDate = new Date();
        float mockTotalFactures = 500.0f;
        float mockTotalRecouvrement = 450.0f;
        float expectedPercentage = (mockTotalRecouvrement / mockTotalFactures) * 100;

        when(factureRepository.getTotalFacturesEntreDeuxDates(startDate, endDate)).thenReturn(mockTotalFactures);
        when(reglementService.getChiffreAffaireEntreDeuxDate(startDate, endDate)).thenReturn(mockTotalRecouvrement);

        float result = factureService.pourcentageRecouvrement(startDate, endDate);

        assertEquals(expectedPercentage, result);
        verify(factureRepository, times(1)).getTotalFacturesEntreDeuxDates(startDate, endDate);
        verify(reglementService, times(1)).getChiffreAffaireEntreDeuxDate(startDate, endDate);
    }


}
