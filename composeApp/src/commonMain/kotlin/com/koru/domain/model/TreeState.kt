package com.koru.domain.model

/**
 * Represents the visual and behavioural state of the Koru tree.
 *
 * The state is derived from the user's capture activity and drives
 * both the tree's animation and its visual appearance on the home screen.
 *
 * Transition rule: the tree enters [HIBERNATING] when no [com.koru.domain.model.Trace]
 * has been captured in the last 14 days. It returns to [ACTIVE] as soon as
 * a new trace is saved.
 *
 * @see com.koru.domain.usecase.ObserveTreeStateUseCase
 */
enum class TreeState {
    /**
     * The tree is alive and growing.
     * Displayed when the user has captured at least one trace within the last 14 days.
     * The tree pulses and animates with each new capture.
     */
    ACTIVE,

    /**
     * The tree has gone dormant due to inactivity.
     * Displayed when no trace has been captured in more than 14 days.
     * The visual style shifts to a muted, still appearance to prompt re-engagement.
     */
    HIBERNATING,
}
