# UI Specification (UI_Spec) - Namaz Tracker (নামায ট্র্যাকার)
**Design System:** iOS Frosted Glassmorphic & Rounded Pill Design System  
**Framework:** Jetpack Compose (Material 3 + Custom Glass Primitives)  
**App Version:** `v2.11.0` (Build 13)

---

## 1. Visual Language & Design Tokens

### 1.1 Color Tokens & Dual Themes
The app supports seamless dark and light modes with custom semantic tokens:

| Token Name | Light Mode Value | Dark Mode Value | Usage Description |
| :--- | :--- | :--- | :--- |
| **`colors.emerald`** | `#059669` (Emerald 600) | `#10B981` (Emerald 500) | Primary brand accent, active selection, success pills |
| **`colors.emeraldSoft`** | `rgba(5, 150, 105, 0.12)` | `rgba(16, 185, 129, 0.16)` | Background for badges, chips, and counters |
| **`colors.background`** | `#F8FAFC` (Slate 50) | `#070D0A` (Deep Forest Black) | Primary app screen background |
| **`colors.card`** | `#FFFFFF` | `#0E1612` (Card Dark Surface) | Primary elevated container surface |
| **`colors.cardSubtle`** | `#F1F5F9` (Slate 100) | `#141F19` (Dark Card Subtle) | Secondary cards, pill trays, and segmented docks |
| **`colors.border`** | `rgba(203, 213, 225, 0.6)` | `rgba(255, 255, 255, 0.08)` | Subdued container rims and dividers |
| **`colors.textPrimary`** | `#0F172A` (Slate 900) | `#F8FAFC` (Slate 50) | Primary headlines and high-emphasis body text |
| **`colors.textSecondary`** | `#475569` (Slate 600) | `#94A3B8` (Slate 400) | Secondary labels, descriptions, and timestamps |
| **`colors.textMuted`** | `#64748B` (Slate 500) | `#64748B` (Slate 500) | Unselected tab text, placeholders, auxiliary notes |

---

## 2. iOS Frosted Glassmorphism & Pill Architecture

The app eliminates boxy rectangular buttons in favor of Apple-inspired translucent glass pills.

### 2.1 Component Specifications

#### A. `IosGlassPillButton`
- **Shape**: `RoundedCornerShape(999.dp)` (Symmetric Capsule).
- **Surface Gradient**:
  - *Primary (Emerald)*: Multi-stop linear gradient from `0xFF10B981` (0.88 alpha) to `0xFF059669` (0.96 alpha).
  - *Secondary (Glass/Neutral)*: Multi-stop translucent background with high specular transparency.
- **Rim Gradient Border**: Multi-stop `Brush.linearGradient` simulating a top-left light source:
  - White `0.45f` alpha to White `0.12f` alpha on 1.2dp border stroke.
- **Micro-Interaction Animation**:
  - Spring-based bouncy scale on press: `scale = 0.95f` using `Spring.DampingRatioMediumBouncy` and `Spring.StiffnessLow`.
  - Haptic feedback trigger on touch down (`HapticUtil.lightTick`).

#### B. `IosGlassChip`
- **Shape**: `RoundedCornerShape(999.dp)`.
- **Purpose**: Waqt status selection (Jama'at, On-time, Late, Missed), category tags, and search filters.
- **Selected State**: Tinted emerald glow with subtle colored elevation shadow and active 1.0dp rim.
- **Unselected State**: Subdued frosted translucent surface.

#### C. `IosGlassSegmentedControl`
- **Outer Dock**: `RoundedCornerShape(999.dp)` capsule container with 1.0dp border and 4.0dp inner padding.
- **Active Segment**: Floating pill indicator with 4.dp colored spot shadow, white bold typography, and spring transition.
- **Usage**: Sub-tab switchers across Quran/Hadith, Tracker periods (Weekly/Monthly/Yearly/History), and Qaza steppers vs analytics.

---

## 3. Navigation Bar Architecture (`FinancyNavBar.kt`)

The bottom navigation is engineered as an **iOS Floating Frosted Glass Dock**:
- **Dimensions**: Centered floating capsule, `padding(horizontal = 20.dp, vertical = 12.dp)`, height ~68.dp.
- **Shape**: `RoundedCornerShape(999.dp)`.
- **Backdrop**: Translucent surface (`alpha = 0.78f`) with 1.2dp crystalline specular border.
- **Active Tab Pill**:
  - Upgraded to `RoundedCornerShape(999.dp)` capsule.
  - Active background: `colors.emerald.copy(alpha = 0.16f)` with emerald gradient border (`alpha = 0.45f`).
  - Tactile spring bouncy scale (`0.95f` on tap).
- **Navigation Icons**: 100% Pure Android Vector Drawables with dual Filled (active) and Outlined (inactive) states:
  - **Tab 1 (Prayers)**: `ic_nav_namaz_filled` / `ic_nav_namaz_outlined` (Dome & Minaret Mosque motif).
  - **Tab 2 (Quran)**: `ic_nav_quran_filled` / `ic_nav_quran_outlined` (Open Sacred Book with ribbon & ayah star).
  - **Tab 3 (Ibadah)**: `ic_ibadah_filled` / `ic_ibadah_outlined` (Two cupped praying hands with crescent & star).
  - **Tab 4 (Tracker)**: `ic_nav_tracker_filled` / `ic_nav_tracker_outlined` (Minimalist 3-bar analytics chart with upward trend arrow).
  - **Tab 5 (More)**: `ic_nav_more_filled` / `ic_nav_more_outlined` (4-tile utility grid).

---

## 4. Typography Scale

- **App Titles & Hero Countdown**: 24.sp - 28.sp (FontWeight.Bold)
- **Card Titles & Surah Headers**: 15.sp - 17.sp (FontWeight.Bold)
- **Arabic Script (Amiri / Uthmani)**: 20.sp - 24.sp (FontWeight.Normal, lineHeight = 36.sp)
- **Body & Transliteration**: 12.5.sp - 14.sp (FontWeight.Medium)
- **Pills, Chips, & Badges**: 10.5.sp - 12.sp (FontWeight.Bold)
