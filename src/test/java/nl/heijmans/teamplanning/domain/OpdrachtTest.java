package nl.heijmans.teamplanning.domain;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class OpdrachtTest {

    Monteur monteurA, monteurB;
    List<Monteur> monteursToAdd;
    Ploeg ploeg;
    String bestekcode;
    Opdracht opdracht;
        
    @BeforeEach                                         
    void setUp() {
        monteurA = new Monteur("A", new Skill(1, "VIAG VP"));
        monteurB = new Monteur("B", new Skill(2, "VIAG VOP meters"));
        monteursToAdd = new ArrayList<>(List.of(monteurA, monteurB));

        ploeg = new Ploeg();
        ploeg.setNaam("TestPloeg");
        ploeg.setMonteurs(monteursToAdd);

        // @RequirementTranslator for E1688 (r32) -> tmp.put("E1688", List.of(
            // new OpdrachtRequirement(4,1,3,"BEI VP"),
            // new OpdrachtRequirement(6,1,3,"BEI VOP")));
        bestekcode = "E1688";
        opdracht = new Opdracht();
    }

    @Test
    void testReqsFromBestekcode() {
        assertNull(opdracht.getBestekcode());
        assertNull(opdracht.getrequiredSkills());
        opdracht.setBestekcode(bestekcode);
        assertEquals("E1688", opdracht.getBestekcode(), "Verwachte bestekcode");

        opdracht.ReqsFromBestekcode();
        assertEquals(2, opdracht.getrequiredSkills().size(), "Bevestiging twee skills");
        
        assertAll("Verifieer OpdrachtRequirement typenummer",
            () -> assertEquals(4, opdracht.getrequiredSkills().get(0).getTypenummer()),
            () -> assertEquals(6, opdracht.getrequiredSkills().get(1).getTypenummer())
        );

        opdracht.setDuurFromSkills();
        assertEquals(3, opdracht.getDurationInHours(), "Duur opdracht overeenkomstig met skills");

    }
}
