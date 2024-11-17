import org.apache.commons.jexl3.*;

import java.util.*;

public class JEXLSampleApp2 {

    private static final JexlEngine jexl = new JexlBuilder().cache(512).strict(true).silent(false).create();

    public static void main(String[] args) {
        // PR is linked to PN1, PN2, PF1 and PF2
        Collection<String> pfItems = Arrays.asList("PF1", "PF2");
        Collection<String> pnItems = Arrays.asList("PN1", "PN2");

        // U has PF_R on PF1 => PF1 : {PF_R}
        // U has PF_C, PF_R, PF_U and PF_D on PF2 => PF2 : {PF_C, PF_R, PF_U, PF_D}
        Map<String, Collection<String>> pfPermissions = new HashMap<>();
        pfPermissions.put("PF1", List.of("PF_R"));
        pfPermissions.put("PF2", Arrays.asList("PF_C", "PF_R", "PF_U", "PF_D"));

        // U has {PN_C, PN_R, PN_U, PN_D} on PN1 => PN1 : {PN_C, PN_R, PN_U, PN_D}
        // U has {PN_R} on PN2 => PF2 : {PN_R}
        Map<String, Collection<String>> pnPermissions = new HashMap<>();
        pnPermissions.put("PN1", Arrays.asList("PN_C", "PN_R", "PN_U", "PN_D"));
        pnPermissions.put("PN2", List.of("PN_R"));

        // populate the context
        JexlContext context = new MapContext();
        context.set("pfItems", pfItems);
        context.set("pnItems", pnItems);
        context.set("pfPermissions", pfPermissions);
        context.set("pnPermissions", pnPermissions);

        // PR is visible if
        // - PF_U on one PF
        // - AND
        // - PN_R on all PN

        // Create an expression object
        String script = """
                var any = (items, permissions, right) -> {
                    for (item : items) {
                        if (item =~ permissions && right =~ permissions[item]) {
                            return true;
                        }
                    }
                    return false;
                }
                var all = (items, permissions, right) -> {
                    for (item : items) {
                        if (item !~ permissions && right !~ permissions[item]) {
                            return false;
                        }
                    }
                    return true;
                }
                return any(pfItems, pfPermissions, 'PF_U') && all(pnItems, pnPermissions, 'PN_R');
                """;

        JexlScript jexlScript = jexl.createScript(script);

        // work it out
        Boolean isVisible = (Boolean) jexlScript.execute(context);
        System.out.println("isVisible=" + isVisible);
    }
}
