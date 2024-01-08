package nl.heijmans.teamplanning.domain;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

import org.junit.jupiter.api.Test;

    // Skill
    // +------------+--------------+
    // | int        | String       |
    // +------------+--------------+
    // | typenummer | omschrijving |
    // +------------+--------------+

    // PloegSkill
    // +------------+--------+--------------+
    // | int        | int    | String       |
    // +------------+--------+--------------+
    // | typenummer | aantal | omschrijving |
    // +------------+--------+--------------+

class PloegSkillTest {

    Monteur monteurA, monteurB, monteurC, monteurD, monteurE;
    List<Monteur> monteursToAdd;
    Ploeg ploeg;
    
    @BeforeEach                                         
    void setUp() {
        monteurA = new Monteur("A", new Skill(1, "VIAG VP"));
        monteurB = new Monteur("B", new Skill(2, "VIAG VOP meters"));
        monteurC = new Monteur("A", new Skill(1, "VIAG VP"));
        monteurD = new Monteur("B", new Skill(2, "VIAG VOP meters"));
        monteurE = new Monteur("B", new Skill(2, "VIAG VOP meters"));
        monteursToAdd = new ArrayList<>(List.of(monteurA, monteurB, monteurC, monteurD, monteurE));

        ploeg = new Ploeg();
        ploeg.setNaam("TestPloeg");
        ploeg.setMonteurs(monteursToAdd);
    }

    @Test
    @DisplayName("Ploegskills")
    void setSkillsTest() {
        // Controleer of ploegskills nog leeg zijn
        assertNull(ploeg.getPloegSkill());

        // Skills per monteur beschikbaar in ploeg:
        // +------------+-----------------+
        // | typenummer | omschrijving    |
        // +------------+-----------------+
        // | 1          | VIAG VP         |
        // +------------+-----------------+
        // | 2          | VIAG VOP meters |
        // +------------+-----------------+
        // | 1          | VIAG VP         |
        // +------------+-----------------+
        // | 2          | VIAG VOP meters |
        // +------------+-----------------+
        // | 2          | VIAG VOP meters |
        // +------------+-----------------+

        // Verwachte output PloegSkill:
        // +------------+--------+-----------------+
        // | typenummer | aantal | omschrijving    |
        // +------------+--------+-----------------+
        // | 1          | 2      | VIAG VP         |
        // +------------+--------+-----------------+
        // | 2          | 3      | VIAG VOP meters |
        // +------------+--------+-----------------+

        ploeg.setPloegSkill();
        ploeg.getPloegSkill().forEach(ploegskill -> System.out.println(ploegskill));

        // Controleer of er inderdaad nog maar twee items overblijven
        assertTrue(ploeg.getPloegSkill().size() == 2);

        // Controleer typenummers op harde plaats
        assertAll("Verifieer typenummers",
            () -> assertEquals(1, ploeg.getPloegSkill().get(0).getTypenummer()),
            () -> assertEquals(2, ploeg.getPloegSkill().get(1).getTypenummer())
        );

        // Controleer aantallen op harde plaats
        assertAll("Verifieer aantallen",
            () -> assertEquals(2, ploeg.getPloegSkill().get(0).getAantal()),
            () -> assertEquals(3, ploeg.getPloegSkill().get(1).getAantal())
        );

        // Controleer beschrijvingen op harde plaats
        assertAll("Verifieer beschrijvingen",
            () -> assertEquals("VIAG VP", ploeg.getPloegSkill().get(0).getOmschrijving()),
            () -> assertEquals("VIAG VOP meters", ploeg.getPloegSkill().get(1).getOmschrijving())
        );
    }
}