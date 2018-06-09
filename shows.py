import time

import math


class ColorResult:
    r = 0
    g = 0
    b = 0

    def __init__(self, r, g, b):
        self.r = r
        self.g = g
        self.b = b


class FadeShow:
    def __init__(self, parent):
        self.parent = parent
        self.running = False

    def run(self, startedAt, pauseTime, fadeTime, colours):
        self.startTime = startedAt
        self.pauseTime = pauseTime
        self.fadeTime = fadeTime
        self.c = colours
        self.running = True
        while self.running:
            dT = (time.time()) - self.startTime
            wV = dT % (self.c.__len__() * (self.pauseTime + self.fadeTime))
            p = int(math.floor(wV / (self.pauseTime + self.fadeTime)))
            if wV - (p * (self.pauseTime + self.fadeTime)) > self.pauseTime:
                fP = (wV - (p * (self.pauseTime + self.fadeTime))) - self.pauseTime
                pO = p + 1
                if p >= self.c.__len__() - 1:
                    pO = 0
                self.setColor(self.interp(self.c[p], self.c[pO], fP, self.fadeTime))
            else:
                self.setColor(self.c[p])
        self.setColor(ColorResult(0, 0, 0))

    def setColor(self, result):
        # print("[" + str(round(result.r, 2)) + "," + str(round(result.g, 2)) + "," + str(round(result.b, 2)) + "]")
        self.parent.updateColor(result)

    def stop(self):
        self.running = False
        print(self.running)

    # Linear interpolation between two values.
    def interpolate(self, startValue, endValue, stepNumber, lastStepNumber):
        if (stepNumber > lastStepNumber):
            return endValue
        return (endValue - startValue) * stepNumber / lastStepNumber + startValue

    # Interpolate between two colours and return the new colour as a ColorResult.
    def interp(self, colorOne, colorTwo, step, stepMax):
        r = self.interpolate(colorOne.r, colorTwo.r, step, stepMax)
        g = self.interpolate(colorOne.g, colorTwo.g, step, stepMax)
        b = self.interpolate(colorOne.b, colorTwo.b, step, stepMax)
        return ColorResult(r, g, b)


class FlashShow:
    def __init__(self, parent):
        self.parent = parent
        self.running = False

    def run(self, startedAt, duration, color, fade,repeat):
        self.startTime = startedAt
        self.duration = duration
        self.color = color
        self.fade = fade
        self.running = True
        self.repeat = repeat

        self.pauseTime = 0
        self.fadeTime = self.duration/2
        self.c = [ColorResult(0,0,0),color,ColorResult(0,0,0)]

        while self.running:
            dT = (time.time()) - self.startTime
            if self.fade == "False":
                self.setColor(color)
            else:
                wV = dT % (self.c.__len__() * (self.pauseTime + self.fadeTime))
                p = int(math.floor(wV / (self.pauseTime + self.fadeTime)))
                if wV - (p * (self.pauseTime + self.fadeTime)) > self.pauseTime:
                    fP = (wV - (p * (self.pauseTime + self.fadeTime))) - self.pauseTime
                    pO = p + 1
                    if p >= self.c.__len__() - 1:
                        pO = 0
                    self.setColor(self.interp(self.c[p], self.c[pO], fP, self.fadeTime))
                else:
                    self.setColor(self.c[p])
            if dT > self.duration and self.repeat == False:
                self.stop()
        self.setColor(ColorResult(0, 0, 0))


    def setColor(self, result):
        # print("[" + str(round(result.r, 2)) + "," + str(round(result.g, 2)) + "," + str(round(result.b, 2)) + "]")
        self.parent.updateColor(result)


    def stop(self):
        self.running = False
        print(self.running)


    # Linear interpolation between two values.
    def interpolate(self, startValue, endValue, stepNumber, lastStepNumber):
        if (stepNumber > lastStepNumber):
            return endValue
        return (endValue - startValue) * stepNumber / lastStepNumber + startValue


    # Interpolate between two colours and return the new colour as a ColorResult.
    def interp(self, colorOne, colorTwo, step, stepMax):
        r = self.interpolate(colorOne.r, colorTwo.r, step, stepMax)
        g = self.interpolate(colorOne.g, colorTwo.g, step, stepMax)
        b = self.interpolate(colorOne.b, colorTwo.b, step, stepMax)
        return ColorResult(r, g, b)
