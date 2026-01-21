/*
 * Copyright (c) 2021 Sebastian Erives
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */
@file:Suppress("UNUSED")

package org.deltacv.mai18n.test

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import org.deltacv.mai18n.*

class LoadTests : StringSpec({
    lateinit var languageEn: Language
    lateinit var languageEs: Language

    "Load en test.csv" {
        languageEn = Language("/test.csv", "en", loadLazily = false)
        languageEn.strings.size shouldBe 8
    }
    "Load es test.csv" {
        languageEs = Language("/test.csv", "es", loadLazily = false)
        languageEn.strings.size shouldBe 8
    }

    "Checking \"en\" Strings" {
        languageEn.get("test1") shouldBe test1_en
        languageEn.get("test2") shouldBe test2_en
        languageEn.get("test3") shouldBe test3_en
        languageEn.get("test4") shouldBe test4_en
        languageEn.get("test5") shouldBe test5_en
    }

    "Checking \"es\" Strings" {
        languageEs.get("test1") shouldBe test1_es
        languageEs.get("test2") shouldBe test2_es
        languageEs.get("test3") shouldBe test3_es
        languageEs.get("test4") shouldBe test4_es
        languageEs.get("test5") shouldBe test5_es
    }
})

class TrTests : StringSpec({
    val languageEn = Language("/test.csv", "en")

    "Making LangManager a global tr" {
        languageEn.makeGlobalTr()
        globalTrLanguage shouldBe languageEn
    }

    "Basic tr" {
        languageEn.makeGlobalTr()
        globalTrLanguage shouldBe languageEn

        tr("funny copypasta $[test1]")             shouldBe "funny copypasta $test1_en"
        tr("sunshine $[test2] mm")                 shouldBe "sunshine $test2_en mm"
        tr("another funny copypasta $[test3]")     shouldBe "another funny copypasta $test3_en"
        tr("(another funny copypasta)^2 $[test4]") shouldBe "(another funny copypasta)^2 $test4_en"
        tr("(another funny copypasta)^3 $[test5]") shouldBe "(another funny copypasta)^3 $test5_en"
    }

    "Formatting tr" {
        languageEn.makeGlobalTr()
        globalTrLanguage shouldBe languageEn

        tr("test_formatting1", "gf leek", 2607) shouldBe "I would like a gf leek for $2607 please"
        tr("test_formatting2", "duckus", 2607)  shouldBe "I have duckus ducks"
        tr("test_formatting3", 1387.492)        shouldBe "We have 1387.492 more days to go"
    }


    "Caching check" {
        languageEn.makeGlobalTr()

        invalidateStringVarCache()
        languageEn.invalidateCache()

        repeat(100) {
            tr("test_formatting1", "mai", 2807)
            tr("test_formatting1", "mai", Math.random())
        }

        stringVarCache.size shouldBe 101 // 1 for the consistent call + 100 random numbers
        languageEn.trCache.size shouldBe 1 // 1 for "test_formatting1"
    }
})

const val test1_en = "It sure would be nice if Gluten Free truly designed, built and programmed their own robots instead of having their coach do it for them."
const val test1_es = "Definitivamente seria bueno si Gluten Free realmente hubiera diseñado, construido y programado sus robots en lugar de que su coach lo hiciera por ellos."

const val test2_en = "Welcome to your good old home my dear, we missed you so much."
const val test2_es = "Bienvenida a tu antigua casa querida, te extrañamos tanto."

const val test3_en = "I thought that was the intent of the First competitions.  Having coaches do the work and the kids just drive the robot in competition defeats the entire intent of the competition."
const val test3_es = "Pense que esa era la intencion de las competencias de First. Cuando los coaches hacen todo el trabajo y los niños solo manejan el robot en la competencia, le quita todo el sentido a la competencia."

const val test4_en = "You wake up, and realize: it's competition day. Flying through your tasks, your team shows up at comp with an epic robot, tested and ready for ultimate goal action. But something feels off today, and your spine tingles. Setting up the pit, you glance around the room. Far in the corner, your eyes meet with someone else, carrying a robot. You feel a shiver.\n" +
        "Finished setting up your pit, you sign up for a spot at the practice fields immediately. Your robot functions as built! This will be a great competition. You're called up for your first match of the day. Setting up your robot on the field, you get the shiver again. Slowly looking up, you see the kid from before is your alliance partner. They carefully take a robot out of a box and carefully place it down on the field. It's beyond human comprehension. The robot was made entirely out of RGB strips. The mecanums, the channel, the mechanisms, even the team number. \"oh god no\" you say before you hear a clank from besides you. The kid begins to set up a massive driver station. It has giant 3d printed joysticks and hundreds of 3d printed buttons actuating precise combinations on two different controllers. It's all over. They tap a button on the phone. You hear a low whining noise and the ground begins to shake. The robot on the field begins to start glowing. You hear the floor cracking under its weight. As it lights up, you see them furiously tapping buttons on the omega driver station and laughing hysterically. You have no choice but to cover your eyes as the RGB strips release a blinding blaze of light. You hear pieces of the ceiling begin to give way. As the audience falls into chaos, you can't help but hear a faint sound in the sky. Through the blinding light you see a meteor in the distance. On further inspection, it's no meteor, but rather a large hammer. A thundering boom envelopes the field and the sound of mods yelling \"OFF TOPIC\" echoes through the land. The floor finally gives way and you fall into the abyss. F"
const val test4_es = "Te despiertas y te das cuenta: es el día de la competencia. Volando a través de sus tareas, su equipo se presenta en la competición con un robot épico, probado y listo para la acción del objetivo final. Pero algo se siente mal hoy y su columna vertebral hormiguea. Preparando el pozo, miras alrededor de la habitación. En el rincón, tus ojos se encuentran con los de otra persona que lleva un robot. Sientes un escalofrío.\n" +
        "Terminado de configurar tu pozo, te inscribes para un lugar en los campos de práctica de inmediato. ¡Su robot funciona como está construido! Esta será una gran competencia. Estás convocado para tu primer partido del día. Al configurar su robot en el campo, vuelve a tener escalofríos. Lentamente mirando hacia arriba, ves que el niño de antes es tu compañero de alianza. Sacan con cuidado un robot de una caja y lo colocan con cuidado en el campo. Está más allá de la comprensión humana. El robot está hecho completamente de tiras RGB. Los mecanums, el canal, los mecanismos, incluso el número de equipo. \"oh dios no\" dices antes de escuchar un ruido metálico a tu lado. El niño comienza a montar una estación de conducción masiva. Tiene joysticks gigantes impresos en 3D y cientos de botones impresos en 3D que activan combinaciones precisas en dos controladores diferentes. Se acabo. Tocan un botón en el teléfono. Oyes un gemido bajo y el suelo comienza a temblar. El robot en el campo comienza a brillar. Escuchas el suelo crujir bajo su peso. Cuando se enciende, los ve presionando furiosamente los botones de la estación del conductor omega y riendo histéricamente. No tiene más remedio que cubrirse los ojos mientras las tiras RGB liberan un destello de luz cegadora. Escuchas pedazos del techo que comienzan a ceder. Mientras la audiencia cae en el caos, no puedes evitar escuchar un leve sonido en el cielo. A través de la luz cegadora ves un meteoro en la distancia. En una inspección más profunda, no es un meteoro, sino un gran martillo. Un boom atronador envuelve el campo y el sonido de mods gritando \"OFF TOPIC\" hace eco a través de la tierra. El suelo finalmente cede y caes al abismo. F"

const val test5_en = "So you're going by \"loltyler1\" now nerd? Haha whats up douche bag, it's Tanner from Highschool. Remember me? Me and the guys used to give you a hard time in school. Sorry you were just an easy target lol. I can see not much has changed. Remember Sarah the girl you had a crush on? Yeah we're married now. I make over 200k a year and drive a mustang GT. I guess some things never change huh loser? Nice catching up lol. Pathetic."
const val test5_es = "¿Entonces vas a llamar \"loltyler1\" ahora nerd? Jaja, qué pasa idiota, es Tanner de la escuela secundaria. ¿Recuérdame? Los chicos y yo solíamos hacerte pasar un mal rato en la escuela. Lo siento, solo fuiste un objetivo fácil lol. Puedo ver que no ha cambiado mucho. ¿Recuerdas a Sarah, la chica de la que estabas enamorado? Sí, ahora estamos casados. Gano más de 200.000 al año y conduzco un mustang GT. Supongo que algunas cosas nunca cambian, ¿eh, perdedor? Agradable ponerse al día lol. Patético."