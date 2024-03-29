package com.uroad.dubai.utils

import android.animation.*
import android.view.View

class AnimUtils {
    companion object {
        fun scaleX(target: View, duration: Long): ObjectAnimator {
            return ObjectAnimator.ofPropertyValuesHolder(target, scaleXHolder()).apply {
                this.duration = duration
                this.repeatCount = ValueAnimator.INFINITE
            }
        }

        private fun scaleXHolder(): PropertyValuesHolder {
            return PropertyValuesHolder.ofKeyframe(View.SCALE_X,
                    Keyframe.ofFloat(0f, 1f),
                    Keyframe.ofFloat(.1f, .9f),
                    Keyframe.ofFloat(.2f, .9f),
                    Keyframe.ofFloat(.3f, 1.1f),
                    Keyframe.ofFloat(.4f, 1.1f),
                    Keyframe.ofFloat(.5f, 1.1f),
                    Keyframe.ofFloat(.6f, 1.1f),
                    Keyframe.ofFloat(.7f, 1.1f),
                    Keyframe.ofFloat(.8f, 1.1f),
                    Keyframe.ofFloat(.9f, 1.1f),
                    Keyframe.ofFloat(1f, 1f)
            )
        }

        fun scaleY(target: View, duration: Long): ObjectAnimator {
            return ObjectAnimator.ofPropertyValuesHolder(target, scaleYHolder()).apply {
                this.duration = duration
                this.repeatCount = ValueAnimator.INFINITE
            }
        }

        private fun scaleYHolder(): PropertyValuesHolder {
            return PropertyValuesHolder.ofKeyframe(View.SCALE_Y,
                    Keyframe.ofFloat(0f, 1f),
                    Keyframe.ofFloat(.1f, .9f),
                    Keyframe.ofFloat(.2f, .9f),
                    Keyframe.ofFloat(.3f, 1.1f),
                    Keyframe.ofFloat(.4f, 1.1f),
                    Keyframe.ofFloat(.5f, 1.1f),
                    Keyframe.ofFloat(.6f, 1.1f),
                    Keyframe.ofFloat(.7f, 1.1f),
                    Keyframe.ofFloat(.8f, 1.1f),
                    Keyframe.ofFloat(.9f, 1.1f),
                    Keyframe.ofFloat(1f, 1f)
            )
        }

        fun rotate(target: View): ObjectAnimator {
            return rotate(target, 2f, 1000L)
        }

        fun rotate(target: View, duration: Long): ObjectAnimator {
            return rotate(target, 2f, duration)
        }

        fun rotate(target: View, shakeFactor: Float, duration: Long): ObjectAnimator {
            return ObjectAnimator.ofPropertyValuesHolder(target, rotateHolder(shakeFactor)).apply {
                this.duration = duration
                this.repeatCount = ValueAnimator.INFINITE
            }
        }

        private fun rotateHolder(shakeFactor: Float): PropertyValuesHolder {
            return PropertyValuesHolder.ofKeyframe(View.ROTATION,
                    Keyframe.ofFloat(0f, 0f),
                    Keyframe.ofFloat(.1f, -3f * shakeFactor),
                    Keyframe.ofFloat(.2f, -3f * shakeFactor),
                    Keyframe.ofFloat(.3f, 3f * shakeFactor),
                    Keyframe.ofFloat(.4f, -3f * shakeFactor),
                    Keyframe.ofFloat(.5f, 3f * shakeFactor),
                    Keyframe.ofFloat(.6f, -3f * shakeFactor),
                    Keyframe.ofFloat(.7f, 3f * shakeFactor),
                    Keyframe.ofFloat(.8f, -3f * shakeFactor),
                    Keyframe.ofFloat(.9f, 3f * shakeFactor),
                    Keyframe.ofFloat(1f, 0f)
            )
        }

        fun animFull(target: View, shakeFactor: Float, duration: Long): ObjectAnimator {
            return ObjectAnimator.ofPropertyValuesHolder(target, scaleXHolder(), scaleYHolder(), rotateHolder(shakeFactor)).setDuration(duration)
        }
    }
}