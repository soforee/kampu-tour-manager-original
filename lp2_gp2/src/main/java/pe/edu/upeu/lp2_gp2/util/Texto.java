package pe.edu.upeu.lp2_gp2.util;

/** Utilidades de texto. Oracle guarda '' como NULL, así que se normaliza en ambos sentidos. */
public final class Texto {
    private Texto() {
    }

    /** NULL de la BD -> "" en el JSON (el frontend trata estos campos como string). */
    public static String nz(String s) {
        return s == null ? "" : s;
    }

    /** Recorta espacios; vacío -> null. */
    public static String limpio(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }
}
