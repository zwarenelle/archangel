package nl.heijmans.teamplanning.domain;

import java.util.List;

// import jakarta.persistence.CascadeType;
// import jakarta.persistence.Entity;
// import jakarta.persistence.FetchType;
// import jakarta.persistence.GeneratedValue;
// import jakarta.persistence.Id;
// import jakarta.persistence.JoinColumn;
// import jakarta.persistence.OneToMany;

// @Entity
public class Cluster {

    // @Id
    // @GeneratedValue
    // private Long id;

    // @OneToMany(fetch = FetchType.EAGER, cascade=CascadeType.ALL, orphanRemoval=true)
    // @JoinColumn(name="CLUSTER_ID")
    private List<Opdracht> opdrachten;

    public List<Opdracht> getOpdrachten() {
        return this.opdrachten;
    }

    public void setOpdrachten(List<Opdracht> opdrachten) {
        this.opdrachten = opdrachten;
    }
    
}