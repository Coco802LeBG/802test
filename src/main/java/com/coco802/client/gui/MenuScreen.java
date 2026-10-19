package com.coco802.client.gui;

import com.coco802.client.Client802;
import com.coco802.client.config.Config802;
import com.coco802.client.module.BoolSetting;
import com.coco802.client.module.Module802;
import com.coco802.client.module.NumberSetting;
import com.coco802.client.module.Setting;
import com.deathmotion.marlowcrystal.MarlowCrystal;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.DoubleConsumer;

/** Menu 802 Client : fenêtre avec sidebar, recherche, filtres, cartes de modules, mods, profils. */
public final class MenuScreen extends Screen {
    private static final int ACCENT = 0xFF2E9BFF;
    private static final int[] R4 = {4, 2, 1, 1};
    private static final int[] R2 = {2, 1};
    private static final String[] NAV = {"Modules", "Settings", "Mods", "Profiles"};
    private static final String[] FILTERS = {"All", "PvP", "HUD", "Visuals", "Player"};
    private static final String[] CATS = {"PvP", "HUD", "Visuals", "Player", "Misc"};
    private static final int[] SCALES = {0, 1, 2, 3, 4, 5, 6};

    // icônes 9x9 : shield, HUD, sparkle, chevrons, lignes
    private static final String[][] ICONS = {
        {".#######.", ".#.....#.", ".#.....#.", ".#.....#.", ".#.....#.", "..#...#..", "..#...#..", "...#.#...", "....#...."},
        {"#########", "#.......#", "#########", "#.......#", "#.#####.#", "#.......#", "#.#####.#", "#.......#", "#########"},
        {"....#....", "....#....", "..#.#.#..", "...###...", "#########", "...###...", "..#.#.#..", "....#....", "....#...."},
        {"#...#....", ".#...#...", "..#...#..", "...#...#.", "....#...#", "...#...#.", "..#...#..", ".#...#...", "#...#...."},
        {".........", "#########", ".........", ".#######.", ".........", "#########", ".........", ".#######.", "........."}
    };

    private record Hit(int x1, int y1, int x2, int y2, DoubleConsumer press, DoubleConsumer drag) {
        boolean contains(int x, int y) { return x >= x1 && x < x2 && y >= y1 && y < y2; }
    }

    private final Screen parent;
    private final long openedAt = System.currentTimeMillis();
    private final List<Hit> hits = new ArrayList<>();
    private TextFieldWidget search;
    private String query = "";
    private String filter = "All";
    private int page = 0;
    private Module802 detail;
    private Hit dragging;
    private boolean wasDown;
    private float alpha = 1f;

    public MenuScreen(Screen parent) {
        super(Text.literal("802 Client"));
        this.parent = parent;
    }

    // ---------- géométrie ----------
    private int pw() { return Math.min(this.width - 16, 560); }
    private int ph() { return Math.min(this.height - 16, 320); }
    private int x0() { return (this.width - pw()) / 2; }
    private int y0() { return (this.height - ph()) / 2; }

    @Override
    protected void init() {
        search = new TextFieldWidget(this.textRenderer, x0() + 112, y0() + 16, 118, 10, Text.literal("Search"));
        search.setDrawsBackground(false);
        search.setText(query);
        search.setChangedListener(s -> query = s);
        this.addSelectableChild(search);
        this.setInitialFocus(search);
    }

    // ---------- primitives ----------
    private static int col(int argb, float a) {
        int al = (int) (((argb >>> 24) & 0xFF) * a);
        return (al << 24) | (argb & 0xFFFFFF);
    }

    private void rect(DrawContext c, int x1, int y1, int x2, int y2, int color) {
        c.fill(x1, y1, x2, y2, col(color, alpha));
    }

    private void rr(DrawContext c, int x1, int y1, int x2, int y2, int[] ins, int color) {
        int n = ins.length;
        if (y2 - y1 < 2 * n || x2 - x1 < 2 * ins[0] + 1) {
            rect(c, x1, y1, x2, y2, color);
            return;
        }
        for (int i = 0; i < n; i++) {
            rect(c, x1 + ins[i], y1 + i, x2 - ins[i], y1 + i + 1, color);
            rect(c, x1 + ins[i], y2 - 1 - i, x2 - ins[i], y2 - i, color);
        }
        rect(c, x1, y1 + n, x2, y2 - n, color);
    }

    private void box(DrawContext c, int x1, int y1, int x2, int y2, int[] ins, int border, int fill) {
        rr(c, x1, y1, x2, y2, ins, border);
        rr(c, x1 + 1, y1 + 1, x2 - 1, y2 - 1, ins, fill);
    }

    private void text(DrawContext c, String s, int x, int y, int color) {
        c.drawTextWithShadow(this.textRenderer, s, x, y, col(color, alpha));
    }

    private void textCentered(DrawContext c, String s, int cx, int y, int color) {
        text(c, s, cx - this.textRenderer.getWidth(s) / 2, y, color);
    }

    private void icon(DrawContext c, int idx, int x, int y, int s, int color) {
        String[] bmp = ICONS[idx];
        for (int j = 0; j < bmp.length; j++)
            for (int i = 0; i < bmp[j].length(); i++)
                if (bmp[j].charAt(i) == '#') rect(c, x + i * s, y + j * s, x + (i + 1) * s, y + (j + 1) * s, color);
    }

    private static int iconFor(String category) {
        for (int i = 0; i < CATS.length; i++) if (CATS[i].equals(category)) return i;
        return 4;
    }

    private void sw(DrawContext c, int x, int y, boolean on) {
        rr(c, x, y, x + 22, y + 10, R2, on ? ACCENT : 0xFF2B3340);
        int kx = on ? x + 13 : x + 1;
        rr(c, kx, y + 1, kx + 8, y + 9, R2, 0xFFFFFFFF);
    }

    // ---------- rendu ----------
    @Override
    public void render(DrawContext ctx, int mx, int my, float delta) {
        hits.clear();
        super.render(ctx, mx, my, delta); // fond flou du jeu
        alpha = Math.max(0.05f, Math.min(1f, (System.currentTimeMillis() - openedAt) / 180f));

        int pw = pw(), ph = ph(), x0 = x0(), y0 = y0();
        box(ctx, x0, y0, x0 + pw, y0 + ph, R4, 0xFF1F2733, 0xF00B0F15);
        rect(ctx, x0 + 92, y0 + 1, x0 + 93, y0 + ph - 1, 0xFF1A212B);
        rect(ctx, x0 + 93, y0 + 40, x0 + pw - 1, y0 + 41, 0xFF1A212B);

        sidebar(ctx, mx, my, x0, y0, pw, ph);
        topbar(ctx, mx, my, x0, y0, pw);

        int x1 = x0 + 102, x2 = x0 + pw - 12, y1 = y0 + 50;
        if (detail != null) detailPage(ctx, x1, x2, y1);
        else if (page == 0) modulesPage(ctx, mx, my, x1, x2, y1);
        else if (page == 1) settingsPage(ctx, x1, x2, y1);
        else if (page == 2) modsPage(ctx, x1, x2, y1);
        else profilesPage(ctx, mx, my, x1, x2, y1);

        search.visible = detail == null && page == 0;
        if (search.visible) {
            if (query.isEmpty()) text(ctx, "Search anything...", x0 + 112, y0 + 16, 0xFF6F7F94);
            search.render(ctx, mx, my, delta);
        }
        handleMouse(mx, my);
    }

    private void sidebar(DrawContext c, int mx, int my, int x0, int y0, int pw, int ph) {
        Text logo = Text.literal("802").formatted(Formatting.BOLD);
        c.drawTextWithShadow(this.textRenderer, logo, x0 + 28, y0 + 11, col(ACCENT, alpha));
        text(c, "CLIENT", x0 + 28, y0 + 22, 0xFFB8C2D0);
        for (int i = 0; i < NAV.length; i++) {
            final int idx = i;
            int y = y0 + 52 + i * 24;
            boolean sel = page == i && detail == null;
            boolean hover = mx >= x0 + 8 && mx < x0 + 86 && my >= y && my < y + 20;
            if (sel) box(c, x0 + 8, y, x0 + 86, y + 20, R4, 0xFF3A4452, 0xFF1A212C);
            else if (hover) rr(c, x0 + 8, y, x0 + 86, y + 20, R4, 0xFF121821);
            text(c, NAV[i], x0 + 16, y + 6, sel ? 0xFFFFFFFF : 0xFF9AA8BA);
            hits.add(new Hit(x0 + 8, y, x0 + 86, y + 20, v -> { page = idx; detail = null; }, null));
        }
        int by = y0 + ph - 28;
        boolean hover = mx >= x0 + 8 && mx < x0 + 86 && my >= by && my < by + 20;
        box(c, x0 + 8, by, x0 + 86, by + 20, R4, 0xFF2B3340, hover ? 0xFF1C2430 : 0xFF141A23);
        text(c, "Edit HUD", x0 + 16, by + 6, 0xFFFFFFFF);
        hits.add(new Hit(x0 + 8, by, x0 + 86, by + 20, v -> this.client.setScreen(new HudEditorScreen(this)), null));
    }

    private void topbar(DrawContext c, int mx, int my, int x0, int y0, int pw) {
        if (detail == null && page == 0) {
            box(c, x0 + 102, y0 + 10, x0 + 236, y0 + 30, R4, 0xFF222B38, 0xFF10151C);
            int cx = x0 + 244;
            for (String f : FILTERS) {
                final String name = f;
                String label = f + " " + count(f);
                int w = this.textRenderer.getWidth(label) + 12;
                boolean sel = filter.equals(f);
                box(c, cx, y0 + 11, cx + w, y0 + 29, R4, sel ? 0xFFB8C2D0 : 0xFF2B3340, sel ? 0xFF1F2733 : 0xFF10151C);
                text(c, label, cx + 6, y0 + 16, sel ? 0xFFFFFFFF : 0xFF9AA8BA);
                hits.add(new Hit(cx, y0 + 11, cx + w, y0 + 29, v -> filter = name, null));
                cx += w + 4;
            }
        } else {
            String title = detail != null ? detail.name : NAV[page];
            text(c, title, x0 + 102, y0 + 16, 0xFFFFFFFF);
        }
        boolean hx = mx >= x0 + pw - 32 && mx < x0 + pw - 6 && my >= y0 + 8 && my < y0 + 32;
        text(c, "x", x0 + pw - 22, y0 + 16, hx ? 0xFFFFFFFF : 0xFF8FA3BC);
        hits.add(new Hit(x0 + pw - 32, y0 + 8, x0 + pw - 6, y0 + 32, v -> this.close(), null));
    }

    private int count(String f) {
        int n = 0;
        for (Module802 m : Client802.MODULES) if (f.equals("All") || m.category().equals(f)) n++;
        return n;
    }

    // ---------- page Modules (grille de cartes) ----------
    private void modulesPage(DrawContext c, int mx, int my, int x1, int x2, int y1) {
        int gap = 8;
        int cw = (x2 - x1 - gap) / 2;
        int ch = 38;
        String q = query.toLowerCase(Locale.ROOT).trim();
        int i = 0;
        for (Module802 m : Client802.MODULES) {
            if (!filter.equals("All") && !m.category().equals(filter)) continue;
            if (!q.isEmpty() && !m.name.toLowerCase(Locale.ROOT).contains(q)
                && !m.description.toLowerCase(Locale.ROOT).contains(q)) continue;
            int cx = x1 + (i % 2) * (cw + gap);
            int cy = y1 + (i / 2) * (ch + gap);
            moduleCard(c, m, cx, cy, cw, ch, mx, my);
            i++;
        }
        if (i == 0) text(c, "Aucun module trouvé.", x1, y1 + 4, 0xFF6F7F94);
    }

    private void moduleCard(DrawContext c, Module802 m, int x, int y, int w, int h, int mx, int my) {
        boolean on = m.isEnabled();
        boolean hover = mx >= x && mx < x + w && my >= y && my < y + h;
        box(c, x, y, x + w, y + h, R4, on ? 0xAA2E9BFF : 0xFF1F2733, hover ? 0xFF171E28 : 0xFF11161D);
        box(c, x + 7, y + 7, x + 31, y + 31, R4, 0xFF222B38, 0xFF1A212C);
        icon(c, iconFor(m.category()), x + 10, y + 10, 2, on ? ACCENT : 0xFFB8C2D0);
        int tw = w - 44 - 38;
        text(c, this.textRenderer.trimToWidth(m.name, tw), x + 38, y + 9, 0xFFFFFFFF);
        text(c, this.textRenderer.trimToWidth(m.description, tw), x + 38, y + 21, 0xFF7F8EA3);
        sw(c, x + w - 30, y + 14, on);
        hits.add(new Hit(x + w - 38, y, x + w, y + h, v -> { m.toggle(); Client802.saveModules(); }, null));
        hits.add(new Hit(x, y, x + w, y + h, v -> {
            if (m.settings.isEmpty()) { m.toggle(); Client802.saveModules(); } else detail = m;
        }, null));
    }

    // ---------- détail d'un module ----------
    private void detailPage(DrawContext c, int x1, int x2, int y) {
        text(c, "< Modules", x1, y - 2, 0xFF8FA3BC);
        hits.add(new Hit(x1, y - 6, x1 + 60, y + 10, v -> detail = null, null));
        y += 16;
        text(c, detail.description, x1, y, 0xFF7F8EA3);
        y += 16;
        final Module802 m = detail;
        y = switchRow(c, "Activé", x1, x2, y, m.isEnabled(), () -> { m.toggle(); Client802.saveModules(); });
        y += 4;
        for (Setting s : m.settings) y = settingRow(c, s, x1, x2, y);
    }

    private int settingRow(DrawContext c, Setting s, int x1, int x2, int y) {
        if (s instanceof BoolSetting b) {
            return switchRow(c, b.label, x1, x2, y, b.get(), () -> b.set(!b.get()));
        } else if (s instanceof NumberSetting n) {
            return slider(c, n.label, x1, x2, y, n.min, n.max, n.step, n.get(), n::set);
        }
        return y;
    }

    // ---------- page Settings ----------
    private void settingsPage(DrawContext c, int x1, int x2, int y) {
        text(c, "GUI scale par écran", x1, y, ACCENT);
        y += 14;
        for (int i = 0; i < GuiScaleManager.KINDS.length; i++) {
            final String kind = GuiScaleManager.KINDS[i];
            int cur = Client802.CONFIG.scaleFor(kind);
            y = cycleRow(c, GuiScaleManager.LABELS[i], x1, x2, y, cur == 0 ? "Auto" : String.valueOf(cur),
                () -> Client802.CONFIG.guiScale.put(kind, step(Client802.CONFIG.scaleFor(kind), -1)),
                () -> Client802.CONFIG.guiScale.put(kind, step(Client802.CONFIG.scaleFor(kind), 1)));
        }
        text(c, "Auto = le GUI scale de tes options Minecraft.", x1, y + 4, 0xFF6F7F94);
        y += 22;
        text(c, "Config : .minecraft/config/802client.json", x1, y, 0xFF6F7F94);
        text(c, "Menu : Shift droit (modifiable dans les contrôles)", x1, y + 12, 0xFF6F7F94);
    }

    private static int step(int cur, int dir) {
        int idx = 0;
        for (int i = 0; i < SCALES.length; i++) if (SCALES[i] == cur) idx = i;
        return SCALES[(idx + dir + SCALES.length) % SCALES.length];
    }

    // ---------- page Mods (installation Modrinth) ----------
    private void modsPage(DrawContext c, int x1, int x2, int y) {
        text(c, "Installe des mods depuis Modrinth (redémarre ensuite le jeu).", x1, y - 2, 0xFF6F7F94);
        y += 14;
        for (ModInstaller.Entry e : ModInstaller.entries()) {
            String st = ModInstaller.status(e);
            boolean can = ModInstaller.canInstall(e);
            box(c, x1, y, x2, y + 28, R4, 0xFF1F2733, 0xFF11161D);
            text(c, e.name(), x1 + 10, y + 5, 0xFFFFFFFF);
            text(c, this.textRenderer.trimToWidth(e.description(), x2 - x1 - 150), x1 + 10, y + 16, 0xFF7F8EA3);
            int bw = 100;
            box(c, x2 - bw - 8, y + 5, x2 - 8, y + 23, R4, can ? ACCENT : 0xFF2B3340, can ? 0xFF16314F : 0xFF141A23);
            textCentered(c, this.textRenderer.trimToWidth(st, bw - 6), x2 - 8 - bw / 2, y + 10, can ? 0xFFFFFFFF : 0xFF9AA8BA);
            if (can) hits.add(new Hit(x2 - bw - 8, y + 5, x2 - 8, y + 23, v -> ModInstaller.install(e), null));
            y += 32;
        }
    }

    // ---------- page Profiles ----------
    private void profilesPage(DrawContext c, int mx, int my, int x1, int x2, int y) {
        text(c, "Tes setups sauvegardés. Clique sur Use pour en changer.", x1, y - 2, 0xFF6F7F94);
        y += 14;
        int gap = 8;
        int cw = (x2 - x1 - 2 * gap) / 3;
        int ch = 74;
        // carte "Save my current setup"
        boolean hover = mx >= x1 && mx < x1 + cw && my >= y && my < y + ch;
        box(c, x1, y, x1 + cw, y + ch, R4, hover ? 0xFF3A4452 : 0xFF1F2733, 0xFF11161D);
        textCentered(c, "+", x1 + cw / 2, y + 16, 0xFFFFFFFF);
        textCentered(c, "Save my setup", x1 + cw / 2, y + 34, 0xFFFFFFFF);
        textCentered(c, "as a profile", x1 + cw / 2, y + 46, 0xFF7F8EA3);
        hits.add(new Hit(x1, y, x1 + cw, y + ch, v -> {
            Client802.saveModules();
            Client802.CONFIG.profiles.add(Client802.CONFIG.snapshot("Profile " + (Client802.CONFIG.profiles.size() + 1)));
            Client802.CONFIG.save();
        }, null));

        int i = 1;
        List<Config802.Profile> list = new ArrayList<>(Client802.CONFIG.profiles);
        for (Config802.Profile p : list) {
            int cx = x1 + (i % 3) * (cw + gap);
            int cy = y + (i / 3) * (ch + gap);
            box(c, cx, cy, cx + cw, cy + ch, R4, 0xFF1F2733, 0xFF11161D);
            text(c, this.textRenderer.trimToWidth(p.name, cw - 30), cx + 8, cy + 8, 0xFFFFFFFF);
            text(c, p.modulesOn() + " of " + Client802.MODULES.size() + " modules on", cx + 8, cy + 22, 0xFF7F8EA3);
            boolean hd = mx >= cx + cw - 20 && mx < cx + cw - 4 && my >= cy + 4 && my < cy + 20;
            text(c, "x", cx + cw - 14, cy + 8, hd ? 0xFFFF6B6B : 0xFF8FA3BC);
            hits.add(new Hit(cx + cw - 20, cy + 4, cx + cw - 4, cy + 20, v -> {
                Client802.CONFIG.profiles.remove(p);
                Client802.CONFIG.save();
            }, null));
            box(c, cx + 8, cy + ch - 26, cx + cw - 8, cy + ch - 8, R4, 0xFF2B3340, 0xFF1A212C);
            textCentered(c, "Use", cx + cw / 2, cy + ch - 21, 0xFFFFFFFF);
            hits.add(new Hit(cx + 8, cy + ch - 26, cx + cw - 8, cy + ch - 8, v -> {
                Client802.CONFIG.apply(p);
                Client802.applyModules();
                Client802.CONFIG.save();
            }, null));
            i++;
        }
    }

    // ---------- lignes réutilisables ----------
    private int switchRow(DrawContext c, String label, int x1, int x2, int y, boolean value, Runnable toggle) {
        box(c, x1, y, x2, y + 22, R4, 0xFF1F2733, 0xFF11161D);
        text(c, label, x1 + 10, y + 7, 0xFFD5DCE6);
        sw(c, x2 - 32, y + 6, value);
        hits.add(new Hit(x1, y, x2, y + 22, v -> toggle.run(), null));
        return y + 26;
    }

    private int cycleRow(DrawContext c, String label, int x1, int x2, int y, String value, Runnable dec, Runnable inc) {
        box(c, x1, y, x2, y + 22, R4, 0xFF1F2733, 0xFF11161D);
        text(c, label, x1 + 10, y + 7, 0xFFD5DCE6);
        text(c, "<", x2 - 124, y + 7, ACCENT);
        int vw = this.textRenderer.getWidth(value);
        text(c, value, x2 - 70 - vw / 2, y + 7, 0xFFFFFFFF);
        text(c, ">", x2 - 14, y + 7, ACCENT);
        final int split = x2 - 70;
        hits.add(new Hit(x2 - 130, y, x2, y + 22, v -> { if (v < split) dec.run(); else inc.run(); }, null));
        return y + 26;
    }

    private int slider(DrawContext c, String label, int x1, int x2, int y, double min, double max, double step,
                       double value, DoubleConsumer setter) {
        box(c, x1, y, x2, y + 22, R4, 0xFF1F2733, 0xFF11161D);
        text(c, label, x1 + 10, y + 7, 0xFFD5DCE6);
        final int bx1 = x2 - 140;
        final int bx2 = x2 - 56;
        double t = Math.max(0, Math.min(1, (value - min) / (max - min)));
        int fx = bx1 + (int) ((bx2 - bx1) * t);
        rr(c, bx1, y + 9, bx2, y + 13, R2, 0xFF2B3340);
        if (fx > bx1 + 1) rr(c, bx1, y + 9, fx, y + 13, R2, ACCENT);
        rr(c, fx - 3, y + 6, fx + 3, y + 16, R2, 0xFFFFFFFF);
        String vs = step >= 1 ? String.valueOf((int) Math.round(value)) : String.format("%.2f", value);
        text(c, vs, bx2 + 10, y + 7, 0xFFFFFFFF);
        DoubleConsumer set = mxv -> {
            double tt = Math.max(0, Math.min(1, (mxv - bx1) / (double) (bx2 - bx1)));
            double v = min + tt * (max - min);
            v = Math.round(v / step) * step;
            setter.accept(Math.max(min, Math.min(max, v)));
        };
        hits.add(new Hit(bx1 - 4, y, bx2 + 4, y + 22, set, set));
        return y + 26;
    }

    /** Souris lue directement (GLFW) : indépendant des changements de signatures de Screen. */
    private void handleMouse(int mx, int my) {
        long handle = this.client.getWindow().getHandle();
        boolean down = GLFW.glfwGetMouseButton(handle, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
        if (down && !wasDown) {
            for (Hit h : hits) {
                if (h.contains(mx, my)) {
                    h.press().accept(mx);
                    dragging = h;
                    break;
                }
            }
        } else if (down && dragging != null && dragging.drag() != null) {
            dragging.drag().accept(mx);
        }
        if (!down && wasDown) {
            dragging = null;
            Client802.CONFIG.save();
            MarlowCrystal.get().saveSettings();
        }
        wasDown = down;
    }

    @Override
    public void close() {
        this.client.setScreen(this.parent);
    }
}
