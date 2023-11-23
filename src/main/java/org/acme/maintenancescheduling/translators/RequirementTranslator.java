package org.acme.maintenancescheduling.translators;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.acme.maintenancescheduling.domain.JobRequirement;

import java.util.List;

public class RequirementTranslator {

    private static final Map<String, List<JobRequirement>> definitions;
    static{
        Map<String, List<JobRequirement>> tmp = new HashMap<String, List<JobRequirement>>();
        tmp.put("E1637", List.of(new JobRequirement(4,1,2,"BEI VP"),new JobRequirement(6,1,2,"BEI VOP")));
        tmp.put("E1665", List.of(new JobRequirement(5,1,1,"BEI VOP meters")));
        tmp.put("E1670", List.of(new JobRequirement(4,1,3,"BEI VP"),new JobRequirement(6,1,3,"BEI VOP")));
        tmp.put("E1671", List.of(new JobRequirement(4,1,3,"BEI VP"),new JobRequirement(6,1,3,"BEI VOP")));
        tmp.put("E1672", List.of(new JobRequirement(4,1,1,"BEI VP"),new JobRequirement(6,1,1,"BEI VOP")));
        tmp.put("E1673", List.of(new JobRequirement(4,1,1,"BEI VP"),new JobRequirement(6,1,1,"BEI VOP")));
        tmp.put("E1674", List.of(new JobRequirement(4,1,3,"BEI VP"),new JobRequirement(6,1,3,"BEI VOP")));
        tmp.put("E1675", List.of(new JobRequirement(4,1,3,"BEI VP"),new JobRequirement(6,1,3,"BEI VOP")));
        tmp.put("E1680", List.of(new JobRequirement(4,1,3,"BEI VP"),new JobRequirement(6,1,3,"BEI VOP")));
        tmp.put("E1681", List.of(new JobRequirement(4,1,3,"BEI VP"),new JobRequirement(6,1,3,"BEI VOP")));
        tmp.put("E1682", List.of(new JobRequirement(4,1,3,"BEI VP"),new JobRequirement(6,1,3,"BEI VOP")));
        tmp.put("E1683", List.of(new JobRequirement(4,1,3,"BEI VP"),new JobRequirement(6,1,3,"BEI VOP")));
        tmp.put("E1684", List.of(new JobRequirement(4,1,3,"BEI VP"),new JobRequirement(6,1,3,"BEI VOP")));
        tmp.put("E1685", List.of(new JobRequirement(4,1,1,"BEI VP"),new JobRequirement(6,1,1,"BEI VOP")));
        tmp.put("E1686", List.of(new JobRequirement(4,1,1,"BEI VP"),new JobRequirement(6,1,1,"BEI VOP")));
        tmp.put("E1687", List.of(new JobRequirement(4,1,3,"BEI VP"),new JobRequirement(6,1,3,"BEI VOP")));
        tmp.put("E1688", List.of(new JobRequirement(4,1,3,"BEI VP"),new JobRequirement(6,1,3,"BEI VOP")));
        tmp.put("E1689", List.of(new JobRequirement(4,1,3,"BEI VP"),new JobRequirement(6,1,3,"BEI VOP")));
        tmp.put("E1690", List.of(new JobRequirement(4,1,1,"BEI VP"),new JobRequirement(6,1,1,"BEI VOP")));
        tmp.put("E1691", List.of(new JobRequirement(4,1,1,"BEI VP"),new JobRequirement(6,1,1,"BEI VOP")));
        tmp.put("E1692", List.of(new JobRequirement(4,1,2,"BEI VP"),new JobRequirement(6,1,2,"BEI VOP")));
        tmp.put("E1693", List.of(new JobRequirement(4,1,3,"BEI VP"),new JobRequirement(6,1,3,"BEI VOP")));
        tmp.put("E1694", List.of(new JobRequirement(4,1,3,"BEI VP"),new JobRequirement(6,1,3,"BEI VOP")));
        tmp.put("E1695", List.of(new JobRequirement(4,1,2,"BEI VP")));
        tmp.put("E1696", List.of(new JobRequirement(4,1,2,"BEI VP"),new JobRequirement(6,1,2,"BEI VOP")));
        tmp.put("E1699", List.of(new JobRequirement(4,1,2,"BEI VP"),new JobRequirement(6,1,2,"BEI VOP")));
        tmp.put("E1701", List.of(new JobRequirement(4,1,3,"BEI VP"),new JobRequirement(6,1,3,"BEI VOP")));
        tmp.put("E1702", List.of(new JobRequirement(4,1,1,"BEI VP"),new JobRequirement(6,1,1,"BEI VOP")));
        tmp.put("G1665", List.of(new JobRequirement(2,1,1,"VIAG VOP meters")));
        tmp.put("G1670", List.of(new JobRequirement(1,1,3,"VIAG VP"),new JobRequirement(3,1,3,"VIAG VOP")));
        tmp.put("G1671", List.of(new JobRequirement(1,1,3,"VIAG VP"),new JobRequirement(3,1,3,"VIAG VOP")));
        tmp.put("G1672", List.of(new JobRequirement(1,1,1,"VIAG VP"),new JobRequirement(3,1,1,"VIAG VOP")));
        tmp.put("G1674", List.of(new JobRequirement(1,1,3,"VIAG VP"),new JobRequirement(3,1,3,"VIAG VOP")));
        tmp.put("G1675", List.of(new JobRequirement(1,1,3,"VIAG VP"),new JobRequirement(3,1,3,"VIAG VOP")));
        tmp.put("G1676", List.of(new JobRequirement(1,1,3,"VIAG VP"),new JobRequirement(3,1,3,"VIAG VOP")));
        tmp.put("G1677", List.of(new JobRequirement(1,1,3,"VIAG VP"),new JobRequirement(3,1,3,"VIAG VOP")));
        tmp.put("G1678", List.of(new JobRequirement(1,1,3,"VIAG VP"),new JobRequirement(3,1,3,"VIAG VOP")));
        tmp.put("G1680", List.of(new JobRequirement(1,1,3,"VIAG VP"),new JobRequirement(3,1,3,"VIAG VOP")));
        tmp.put("G1681", List.of(new JobRequirement(1,1,3,"VIAG VP"),new JobRequirement(3,1,3,"VIAG VOP")));
        tmp.put("G1682", List.of(new JobRequirement(1,1,3,"VIAG VP"),new JobRequirement(3,1,3,"VIAG VOP")));
        tmp.put("G1683", List.of(new JobRequirement(1,1,3,"VIAG VP"),new JobRequirement(3,1,3,"VIAG VOP")));
        tmp.put("G1684", List.of(new JobRequirement(1,1,3,"VIAG VP"),new JobRequirement(3,1,3,"VIAG VOP")));
        tmp.put("G1685", List.of(new JobRequirement(1,1,3,"VIAG VP"),new JobRequirement(3,1,3,"VIAG VOP")));
        tmp.put("G1686", List.of(new JobRequirement(1,1,1,"VIAG VP"),new JobRequirement(3,1,1,"VIAG VOP")));
        tmp.put("G1688", List.of(new JobRequirement(1,1,3,"VIAG VP"),new JobRequirement(3,1,3,"VIAG VOP")));
        tmp.put("G1689", List.of(new JobRequirement(1,1,2,"VIAG VP"),new JobRequirement(3,1,2,"VIAG VOP")));
        tmp.put("G1690", List.of(new JobRequirement(1,1,2,"VIAG VP"),new JobRequirement(3,1,2,"VIAG VOP")));
        tmp.put("G1691", List.of(new JobRequirement(1,1,1,"VIAG VP")));
        tmp.put("G1693", List.of(new JobRequirement(1,1,2,"VIAG VP"),new JobRequirement(3,1,2,"VIAG VOP")));
        tmp.put("G1694", List.of(new JobRequirement(1,1,3,"VIAG VP"),new JobRequirement(3,1,3,"VIAG VOP")));
        tmp.put("G1695", List.of(new JobRequirement(2,1,2,"VIAG VOP meters")));
        tmp.put("G1696", List.of(new JobRequirement(1,1,2,"VIAG VP"),new JobRequirement(3,1,2,"VIAG VOP")));
        tmp.put("G1697", List.of(new JobRequirement(2,1,1,"VIAG VOP meters")));
        tmp.put("G1699", List.of(new JobRequirement(1,1,2,"VIAG VP"),new JobRequirement(3,1,2,"VIAG VOP")));
        tmp.put("G1702", List.of(new JobRequirement(3,1,1,"VIAG VOP")));
        tmp.put("G1704", List.of(new JobRequirement(1,1,3,"VIAG VP"),new JobRequirement(3,1,3,"VIAG VOP")));
        tmp.put("G1705", List.of(new JobRequirement(1,1,3,"VIAG VP"),new JobRequirement(3,1,3,"VIAG VOP")));
        tmp.put("G1706", List.of(new JobRequirement(1,1,3,"VIAG VP"),new JobRequirement(3,1,3,"VIAG VOP")));
        tmp.put("G1707", List.of(new JobRequirement(1,1,3,"VIAG VP"),new JobRequirement(3,1,3,"VIAG VOP")));
        tmp.put("G1708", List.of(new JobRequirement(1,1,3,"VIAG VP"),new JobRequirement(3,1,3,"VIAG VOP")));
        tmp.put("G1712", List.of(new JobRequirement(1,1,3,"VIAG VP"),new JobRequirement(3,1,3,"VIAG VOP")));
        tmp.put("G1714", List.of(new JobRequirement(1,1,3,"VIAG VP"),new JobRequirement(3,1,3,"VIAG VOP")));
        
        definitions = Collections.unmodifiableMap(tmp);
    }
    
    public static final Map<String, List<JobRequirement>> getDefinitions() {
        return definitions;
    }

    public static final List<JobRequirement> getDefinition(String bestekcode)
    {
        return definitions.get(bestekcode);
    }

}