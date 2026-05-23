# Koru — Resumen de Ideas Clave
> Documento de referencia del proceso de ideación. El PRD.md es la fuente de verdad para implementación.
> Última actualización: 2026-05-12

---

## Contexto del Proyecto

Aplicación al **KMP Contest Starter Kit Grant** de la Kotlin Foundation ($30,000). El starter kit es una app de muestra open source que guía a participantes del Kotlin Multiplatform Contest.

**Requisitos técnicos obligatorios:**
- KMP con soporte Android e iOS (web/desktop bienvenido)
- Networking con servicio remoto (REST, Firestore, AI Service)
- Persistencia local (base de datos, preferencias)
- Notificaciones y permisos nativos
- Testing
- Android Gradle Plugin versión 9
- MIT License
- AI-ready (AGENTS.md, SKILLS)

---

## La Idea Central

### Nombre: **"Koru"**
*(Símbolo maorí del helecho que se desenrolla — representa crecimiento y nuevo comienzo)*

**En una línea:**
> Un segundo cerebro de tu mundo interior, donde capturas cómo reaccionas al mundo y la AI detecta tus patrones de pensamiento a lo largo del tiempo.

---

## Conceptos Filosóficos que Fundamentan la App

Estos conceptos no son decorativos — son la arquitectura de pensamiento detrás de cada decisión de producto:

- **Engrama:** La huella neuronal de una experiencia. La unidad mínima que se registra no es *qué pasó* sino *cómo lo interpreté yo*.
- **Principio 90/10:** El 10% es el evento, el 90% es el significado que le damos. La app captura ese 90%.
- **Metacognición:** "Pensar sobre tu pensamiento." El fundamento científico de la app.
- **Reencuadre (Reframing):** La AI ofrece perspectivas alternativas — nunca diagnostica, nunca etiqueta.
- **Legado Transgeneracional:** Lo que construyes puede exportarse y compartirse con generaciones futuras.

---

## Lo que la App Hace — Flujo Definitivo

1. El usuario **captura un trace** — texto o voz — en menos de 10 segundos.
2. La **AI procesa el registro** (previa búsqueda FTS5 local) y devuelve un insight inmediato.
3. Con el tiempo, el **árbol crece visualmente** — cada trace es un nodo nuevo.
4. La AI detecta **patrones metacognitivos** a lo largo del tiempo una vez hay 7+ traces.
5. El usuario puede **exportar su historial** como legado (feature v2).

### Lo que NO es
- No es un diario tradicional (journaling pasivo)
- No es una app de salud mental con ejercicios de CBT genéricos
- No es un second brain de conocimiento externo
- No es solo un tracker de emociones

### Lo que SÍ es
- Un segundo cerebro **emocional y relacional**, no informacional
- Un espejo de tus patrones de pensamiento con evidencia propia
- Una herramienta de metacognición activa, no pasiva

---

## La Metáfora Visual — El Árbol

| Elemento | Significado |
|---|---|
| Tronco | El usuario — el hilo común entre todos los contextos |
| Raíces | Contextos definidos por el usuario (familia, trabajo, yo) |
| Ramas | Traces individuales, posicionados por fecha y contexto |
| Hojas | Insights de AI adjuntos a cada nodo |
| Estado | El árbol pulsa con el uso; hiberna tras 14 días de inactividad |

Implementación: **Compose Canvas** en `commonMain`. Sin renderizado nativo.

---

## Decisiones Tomadas (Preguntas Resueltas)

### ✅ Gesto de captura
**Voz como camino principal, texto como alternativa de igual peso.**
- Pantalla oscura → círculo respirando → mantener presionado y hablar → soltar → trace vuela al árbol
- El text input tiene el mismo peso visual, no es segunda clase
- Filosofía A (captura intencional) como base; Filosofía B (captura en el momento) como feature v2

### ✅ Componente grupal
**Fuera del MVP. Documentado como v2 en README.**
- El core es individual: un usuario, un árbol
- El componente grupal agrega auth multi-usuario, sync en tiempo real y gestión de privacidad — 3+ semanas extra de scope
- El patrón "un usuario, un árbol" es más claro y reutilizable para los concursantes

### ✅ Estrategia anti-abandono (el journaling tiene 80% de abandono en 7 días)
- **Fase 1 (días 1-7):** Habit stacking — el usuario elige un hábito ancla en onboarding (café, antes de dormir, durante el transporte). Notificación local a esa hora.
- **Fase 2 (7+ traces):** Notificaciones de AI. Ej: "Hace 3 semanas capturaste algo que conecta con lo de hoy." La AI tiene suficiente historial para ser relevante.
- **Umbral:** Mínimo 7 traces antes de activar notificaciones de patrones AI.

### ✅ AI service
**Gemini API** vía Ktor Client. Alineado con el ecosistema JetBrains/Google.

---

## Stack Tecnológico Definitivo

Alineado al ecosistema JetBrains/Kotlin Foundation:

| Capa | Tecnología | Razón |
|---|---|---|
| UI | Compose Multiplatform 1.10+ | Flagship de JetBrains para KMP |
| Navegación | Navigation 3 (CMP) | Desarrollado por JetBrains para CMP |
| Networking | Ktor Client | 100% JetBrains, nativo KMP |
| Base de datos | SQLDelight 2.x | Multiplatform-first, FTS5 nativo |
| Serialización | kotlinx.serialization | JetBrains. Gson/Moshi no son KMP |
| Async | kotlinx.coroutines | JetBrains. Estándar absoluto |
| Fechas | kotlinx-datetime | JetBrains. `java.time` no existe en iOS |
| Preferencias | Multiplatform Settings (russhwolf) | Estándar comunitario KMP |
| DI | Koin KMP | Multiplatform-first. Hilt/Dagger son Android-only |
| Testing | kotlin.test + Turbine + MockEngine | kotlin.test es JetBrains |
| Imágenes | Coil 3 | Soporte Compose Multiplatform oficial |
| Logging | Napier | KMP logging. Sin println en producción |
| AI Service | Gemini API (vía Ktor) | JetBrains + Google — relación histórica |
| Build | Gradle + Kotlin DSL, AGP 9 | JetBrains estándar |

---

## Gap de Mercado

| Categoría | Ejemplos | Brecha |
|---|---|---|
| Second brain apps | Notion, Obsidian, Reflect, Mem | Guardan conocimiento externo. No aprenden sobre vos. |
| Apps de metacognición | Ahead, Animi, InsightApp | Ejercicios genéricos. Sin historial personal ni detección de patrones. |
| Journaling apps | Day One, Journey | Registros pasivos. Sin AI insight. 80% abandono en 7 días. |

**Nadie ha construido:** un segundo cerebro que almacene tu mundo interior, aprenda de tus reacciones específicas, y te devuelva insights personalizados basados en tu propio historial.

---

## Estado del Proyecto

| Fase SDD | Estado | Artefacto |
|---|---|---|
| `sdd-init` | ✅ Completo | `AGENTS.md` |
| `sdd-explore` | ✅ Completo | Este documento |
| `sdd-propose` | ✅ Completo | `PRD.md` |
| `sdd-spec` | ✅ Completo | `SPEC.md` |
| `sdd-design` | ✅ Completo | `DESIGN.md` |
| `sdd-tasks` | 🔲 Pendiente | — |
| `sdd-apply` | 🔲 Pendiente | — |

---

## Nota sobre este documento

Este archivo documenta el **proceso de ideación** que llevó a la definición de Koru.
Para implementación, usar siempre el **`PRD.md`** como fuente de verdad.