package com.reactnativenavigation.viewcontrollers.stack

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Activity
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.verify
import com.reactnativenavigation.BaseTest
import com.reactnativenavigation.mocks.SimpleViewController
import com.reactnativenavigation.options.Options
import com.reactnativenavigation.options.params.Bool
import com.reactnativenavigation.utils.createEnterExitAnimation
import com.reactnativenavigation.viewcontrollers.child.ChildControllersRegistry
import com.reactnativenavigation.viewcontrollers.viewcontroller.ViewController
import com.reactnativenavigation.views.element.TransitionAnimatorCreator
import org.assertj.core.api.Java6Assertions.assertThat
import org.junit.Test

class StackAnimatorTest : BaseTest() {
    private lateinit var uut: StackAnimator
    private lateinit var activity: Activity
    private lateinit var child1: SimpleViewController
    private lateinit var child2: SimpleViewController
    private lateinit var commandAnimator: AnimatorSet

    override fun beforeEach() {
        activity = newActivity()
        val transitionAnimatorCreator = mock<TransitionAnimatorCreator> { }
        uut = object : StackAnimator(activity, transitionAnimatorCreator) {
            override fun createAnimatorSet(): AnimatorSet {
                commandAnimator = spy(super.createAnimatorSet())
                return commandAnimator
            }
        }
        val childRegistry = mock<ChildControllersRegistry>()
        child1 = SimpleViewController(activity, childRegistry, "child1", Options())
        child2 = SimpleViewController(activity, childRegistry, "child2", Options())
    }

    @Test
    fun push_onlyEnteringScreenIsAnimatedByDefault() {
        val onAnimationEnd = mock<Runnable>()
        uut.push(child2, child1, Options.EMPTY, onAnimationEnd)

        val pushAnimator = uut.runningPushAnimations[child2]!!
        assertThat(pushAnimator.childAnimations).hasSize(1)
        val appearingAnimatorSet = pushAnimator.childAnimations.first() as AnimatorSet
        (appearingAnimatorSet.childAnimations).forEach {
            assertThat((it as ObjectAnimator).target).isEqualTo(child2.view)
        }
    }

    @Test
    fun push_exitAndEnter() {
        createEnterExitPushAnimation(child2)

        val onAnimationEnd = mock<Runnable>()
        uut.push(child2, child1, child2.options, onAnimationEnd)

        assertThat(commandAnimator.childAnimations).hasSize(2)

        val enter = commandAnimator.childAnimations.first() as AnimatorSet
        (enter.childAnimations).forEach {
            assertThat((it as ObjectAnimator).target).isEqualTo(child2.view)
        }

        val exit = commandAnimator.childAnimations[1] as AnimatorSet
        (exit.childAnimations).forEach {
            assertThat((it as ObjectAnimator).target).isEqualTo(child1.view)
        }
    }

    @Test
    fun push_onAnimationEnd() {
        val onAnimationEnd = mock<Runnable>()
        uut.push(child2, child1, child2.options, onAnimationEnd)

        verify(onAnimationEnd, never()).run()

        commandAnimator.end()
        verify(onAnimationEnd).run()
    }

    @Test
    fun push_waitForRender_appearingScreenIsHiddenUntilAnimationStarts() {
        val onAnimationEnd = mock<Runnable>()
        child2.options.animations.push.waitForRender = Bool(true)

        uut.push(child2, child1, child2.options, onAnimationEnd)

        assertThat(child2.view.alpha).isZero()
        child2.onViewWillAppear()
        assertThat(child2.view.alpha).isOne()
    }

    @Test
    fun pop_onlyExitAnimationIsPlayedByDefault() {
        val onAnimationEnd = mock<Runnable>()
        uut.pop(child1, child2, child2.options.animations.pop, onAnimationEnd)

        assertThat(commandAnimator.childAnimations).hasSize(1)

        val disappearingAnimatorSet = commandAnimator.childAnimations.first() as AnimatorSet
        (disappearingAnimatorSet.childAnimations).forEach {
            assertThat((it as ObjectAnimator).target).isEqualTo(child2.view)
        }
    }

    @Test
    fun pop_enterExitAnimations() {
        createEnterExitPopAnimation(child2)

        val onAnimationEnd = mock<Runnable>()
        uut.pop(child1, child2, child2.options.animations.pop, onAnimationEnd)

        assertThat(commandAnimator.childAnimations).hasSize(2)

        val enter = commandAnimator.childAnimations[1] as AnimatorSet
        (enter.childAnimations).forEach {
            assertThat((it as ObjectAnimator).target).isEqualTo(child1.view)
        }

        val exit = commandAnimator.childAnimations.first() as AnimatorSet
        (exit.childAnimations).forEach {
            assertThat((it as ObjectAnimator).target).isEqualTo(child2.view)
        }
    }

    @Test
    fun setRoot_onlyEnteringScreenIsAnimatedByDefault() {
        val onAnimationEnd = mock<Runnable>()
        uut.setRoot(child2, child2, child1.options, onAnimationEnd)

        assertThat(commandAnimator.childAnimations).hasSize(1)
        val appearingAnimatorSet = commandAnimator.childAnimations.first() as AnimatorSet
        (appearingAnimatorSet.childAnimations).forEach {
            assertThat((it as ObjectAnimator).target).isEqualTo(child2.view)
        }
    }

    @Test
    fun setRoot_enterExitAnimations() {
        createEnterExitSetRootAnimation(child2)

        val onAnimationEnd = mock<Runnable>()
        uut.setRoot(child2, child1, child2.options, onAnimationEnd)

        assertThat(commandAnimator.childAnimations).hasSize(2)

        val enter = commandAnimator.childAnimations[1] as AnimatorSet
        (enter.childAnimations).forEach {
            assertThat((it as ObjectAnimator).target).isEqualTo(child1.view)
        }

        val exit = commandAnimator.childAnimations.first() as AnimatorSet
        (exit.childAnimations).forEach {
            assertThat((it as ObjectAnimator).target).isEqualTo(child2.view)
        }
    }

    @Test
    fun setRoot_onAnimationEnd() {
        val onAnimationEnd = mock<Runnable>()
        uut.setRoot(child2, child1, child2.options, onAnimationEnd)

        verify(onAnimationEnd, never()).run()

        commandAnimator.end()
        verify(onAnimationEnd).run()
    }

    @Test
    fun setRoot_waitForRender() {
        val onAnimationEnd = mock<Runnable>()
        child2.options.animations.setStackRoot.waitForRender = Bool(true)

        uut.setRoot(child2, child1, child2.options, onAnimationEnd)

        verify(commandAnimator, never()).start()
        child2.onViewWillAppear()
        verify(commandAnimator).start()
    }

    @Test
    fun setRoot_waitForRender_appearingScreenIsHiddenUntilAnimationStarts() {
        val onAnimationEnd = mock<Runnable>()
        child2.options.animations.setStackRoot.waitForRender = Bool(true)

        uut.setRoot(child2, child1, child2.options, onAnimationEnd)

        assertThat(child2.view.alpha).isZero()
        child2.onViewWillAppear()
        assertThat(child2.view.alpha).isOne()
    }

    private fun createEnterExitPushAnimation(vc: ViewController<*>) {
        Options().apply {
            animations.push.content = createEnterExitAnimation()
            vc.mergeOptions(this)
        }
    }

    private fun createEnterExitPopAnimation(vc: ViewController<*>) {
        Options().apply {
            animations.pop.content = createEnterExitAnimation()
            vc.mergeOptions(this)
        }
    }

    private fun createEnterExitSetRootAnimation(vc: ViewController<*>) {
        Options().apply {
            animations.setStackRoot.content = createEnterExitAnimation()
            vc.mergeOptions(this)
        }
    }
}