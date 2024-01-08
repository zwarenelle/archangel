package nl.heijmans.teamplanning.domain;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

import org.junit.jupiter.api.Test;

class PloegTest {

    Monteur monteurA, monteurB;
    List<Monteur> monteursToAdd;
    Ploeg ploeg;
    
    @BeforeEach                                         
    void setUp() {
        monteurA = new Monteur("A", new Skill(1, "VIAG VP"));
        monteurB = new Monteur("B", new Skill(2, "VIAG VOP meters"));
        monteursToAdd = new ArrayList<>(List.of(monteurA, monteurB));

        ploeg = new Ploeg();
        ploeg.setNaam("TestPloeg");
        ploeg.setMonteurs(monteursToAdd);
    }

    @Test
    @DisplayName("Ploeg filter")
    // Retourneert een nieuwe tijdelijke ploeg met de meegegeven lijst van Monteurs
    void filterTest() {
        assertEquals(2, ploeg.getMonteurs().size(), "Ploeg zou initieel twee monteurs moeten hebben");

        // Filter de ploeg, behoud alleen monteurA
        ploeg = ploeg.filter(List.of(monteurA));

        assertAll("Verifieer monteur",
            () -> assertEquals(1, ploeg.getMonteurs().size(), "Aantal"),
            () -> assertEquals("A", ploeg.getMonteurs().get(0).getNaam(), "Naam"),
            () -> assertEquals(1, ploeg.getMonteurs().get(0).getVaardigheid().getTypenummer(), "Skill")
        );

        // Zet originele lijst van monteurs terug
        ploeg.setMonteurs(monteursToAdd);

        // Filter de ploeg, behoud alleen monteurB
        ploeg = ploeg.filter(List.of(monteurB));

        assertAll("Verifieer monteur",
            () -> assertEquals(1, ploeg.getMonteurs().size(), "Aantal"),
            () -> assertEquals("B", ploeg.getMonteurs().get(0).getNaam(), "Naam"),
            () -> assertEquals(2, ploeg.getMonteurs().get(0).getVaardigheid().getTypenummer(), "Skill")
        );
    }
}