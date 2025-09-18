let initialTries = 0;

window.addEventListener('DOMContentLoaded', () => {
    // EN UA
    const englishLetters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ'.split('');
    const uaLetters = 'АБВГҐДЕЄЖЗИІЇЙКЛМНОПРСТУФХЦЧШЩЬЮЯ'.split('');
    let currentLetters = englishLetters; // current

    const wordEl = document.getElementById('word');
    const hangmanEl = document.getElementById('hangman');
    const gameOverEl = document.getElementById('game-over');
    const gameOverMessage = document.getElementById('game-over-message');
    const lettersContainer = document.getElementById('letters');
    const languageBtn = document.querySelector('.language-btn');
    const lettersInner = document.getElementById('letters-inner');    // ======== Render letter buttons ========

    function renderLetterButtons(lettersToRender) {
        lettersContainer.innerHTML = '';
        lettersToRender.forEach(letter => {
            const btn = document.createElement('button');
            btn.type = 'button';
            btn.innerText = letter;
            btn.className = 'letter-btn';
            btn.addEventListener('click', () => guessLetter(letter, btn));
            lettersContainer.appendChild(btn);
        });
    }

    // ======== Language switch with flip animation ========
    languageBtn.addEventListener('click', () => {
      const nextLetters = currentLetters === englishLetters ? uaLetters : englishLetters;

      // Переворачиваем контейнер
      lettersInner.style.transform = 'rotateY(180deg)';

      setTimeout(() => {
        renderLetterButtons(nextLetters);
        lettersInner.style.transform = 'rotateY(0deg)';
      }, 300);

      currentLetters = nextLetters;
    });


    // ======== Restart game ========
        gameOverEl.addEventListener('click', () => {
            gameOverEl.style.display = 'none';
            document.querySelectorAll('.letter-btn').forEach(btn => btn.disabled = false);
            startGame();
        });

    renderLetterButtons(currentLetters);
    startGame();

    function disableAllButtons() {
        document.querySelectorAll('.letter-btn').forEach(btn => btn.disabled = true);
    }

    function enableAllButtons() {
        document.querySelectorAll('.letter-btn').forEach(btn => btn.disabled = false);
    }

    // show modal window
    function showGameOverMessage(message) {
        disableAllButtons();
        gameOverMessage.innerText = message;
        gameOverEl.style.display = 'flex';
    }

    // start new game
    async function startGame() {
        try {
            const response = await fetch('/api/hangman/start', { method: 'POST' });
            if (!response.ok) throw new Error('Fail start a new game');

            const result = await response.json();
            initialTries = result.remainingTries;

            wordEl.innerHTML = result.currentState.split('').join(' ');
            hangmanEl.src = `/images/stage-0.png`;
            enableAllButtons();
        } catch (e) {
            console.error('Error start a new game:', e);
        }
    }

    // ======== Confetti ========
    function triggerConfetti() {
        const random = Math.random;
        const cos = Math.cos;
        const sin = Math.sin;
        const PI = Math.PI;
        const PI2 = PI * 2;
        let confetti = [];

        const sizeMin = 3,
              sizeMax = 9,
              deviation = 100,
              dxThetaMin = -0.1,
              dxThetaMax = -dxThetaMin - dxThetaMin,
              dyMin = 0.13,
              dyMax = 0.18,
              dThetaMin = 0.4,
              dThetaMax = 0.7 - dThetaMin;

        const colorThemes = [
            () => `rgb(${200 * random() | 0},${200 * random() | 0},${200 * random() | 0})`,
            () => `rgb(${200},${200*random()|0},${200*random()|0})`,
            () => `rgb(${200*random()|0},${200},${200*random()|0})`,
            () => `rgb(${200*random()|0},${200*random()|0},${200})`
        ];

        function interpolation(a, b, t) {
            return (1 - cos(PI * t)) / 2 * (b - a) + a;
        }

        function createPoisson() {
            const eccentricity = 10;
            let radius = 1 / eccentricity, radius2 = radius + radius;
            let domain = [radius, 1 - radius], measure = 1 - radius2, spline = [0, 1];
            while (measure) {
                let dart = measure * random(), i, l, interval, a, b, c, d;
                for (i = 0, l = domain.length, measure = 0; i < l; i += 2) {
                    a = domain[i]; b = domain[i + 1]; interval = b - a;
                    if (dart < measure + interval) { spline.push(dart += a - measure); break; }
                    measure += interval;
                }
                c = dart - radius; d = dart + radius;
                for (i = domain.length - 1; i > 0; i -= 2) {
                    let left = i - 1; a = domain[left]; b = domain[i];
                    if (a >= c && a < d)
                        if (b > d) domain[left] = d;
                        else domain.splice(left, 2);
                    else if (a < c && b > c)
                        if (b <= d) domain[i] = c;
                        else domain.splice(i, 0, c, d);
                }
                for (i = 0, l = domain.length, measure = 0; i < l; i += 2) measure += domain[i + 1] - domain[i];
            }
            return spline.sort();
        }

        function Confetto(theme) {
            this.frame = 0;
            this.outer = document.createElement('div');
            this.inner = document.createElement('div');
            this.outer.appendChild(this.inner);

            let outerStyle = this.outer.style;
            let innerStyle = this.inner.style;
            outerStyle.position = 'absolute';
            outerStyle.width = outerStyle.height = (sizeMin + sizeMax * random()) + 'px';
            innerStyle.width = innerStyle.height = '100%';
            innerStyle.backgroundColor = theme();

            outerStyle.perspective = '50px';
            outerStyle.transform = `rotate(${360 * random()}deg)`;
            this.axis = `rotate3D(${cos(360 * random())},${cos(360 * random())},0,`;
            this.theta = 360 * random();
            this.dTheta = dThetaMin + dThetaMax * random();

            this.x = window.innerWidth * random();
            this.y = -deviation;
            this.dx = sin(dxThetaMin + dxThetaMax * random());
            this.dy = dyMin + dyMax * random();

            outerStyle.left = this.x + 'px';
            outerStyle.top = this.y + 'px';

            this.splineX = createPoisson();
            this.splineY = [];
            const len = this.splineX.length - 1;
            for (let i = 1; i < len; i++) this.splineY[i] = deviation * random();
            this.splineY[0] = this.splineY[len] = deviation * random();

            this.update = function (height, delta) {
                this.frame += delta;
                this.x += this.dx * delta;
                this.y += this.dy * delta;
                this.theta += this.dTheta * delta;

                let phi = this.frame % 7777 / 7777, i = 0, j = 1;
                while (phi >= this.splineX[j]) i = j++;
                let rho = interpolation(this.splineY[i], this.splineY[j],
                    (phi - this.splineX[i]) / (this.splineX[j] - this.splineX[i]));
                phi *= PI2;

                outerStyle.left = this.x + rho * cos(phi) + 'px';
                outerStyle.top = this.y + rho * sin(phi) + 'px';
                innerStyle.transform = this.axis + this.theta + 'deg)';
                return this.y > height + deviation;
            };
        }

        const container = document.createElement('div');
        container.style.position = 'fixed';
        container.style.top = '0';
        container.style.left = '0';
        container.style.width = '100%';
        container.style.height = '0';
        container.style.overflow = 'visible';
        container.style.zIndex = '9999';
        document.body.appendChild(container);

        for (let i = 0; i < 50; i++) {
            let confetto = new Confetto(colorThemes[Math.floor(random() * colorThemes.length)]);
            confetti.push(confetto);
            container.appendChild(confetto.outer);
        }

        let prev;
        function loop(timestamp) {
            const delta = prev ? timestamp - prev : 0;
            prev = timestamp;
            const height = window.innerHeight;
            for (let i = confetti.length - 1; i >= 0; i--) {
                if (confetti[i].update(height, delta)) {
                    container.removeChild(confetti[i].outer);
                    confetti.splice(i, 1);
                }
            }
            if (confetti.length) requestAnimationFrame(loop);
            else document.body.removeChild(container);
        }
        requestAnimationFrame(loop);
    }


    // ======== Guess letter logic ========
    async function guessLetter(letter, btn) {
        btn.disabled = true;

        try {
            const response = await fetch(`/api/hangman/guess?letter=${encodeURIComponent(letter)}`, { method: 'POST' });
            if (!response.ok) throw new Error('response error');

            const result = await response.json();
            wordEl.innerHTML = result.currentState.split('').join(' ');

            const mistakes = initialTries - result.remainingTries;
            hangmanEl.src = `/images/stage-${mistakes}.png`;

            flashSpotlight(result.wasCorrect);

            if (result.status === 'WON') {
                showGameOverMessage('Continue');
                triggerConfetti();
            } else if (result.status === 'LOST') {
                showGameOverMessage(`💀 You lose! Word: ${result.word}`);
            }
        } catch (e) {
            console.error('Error:', e);
        }
    }

    // ======== Flash spotlight ========
    function flashSpotlight(isCorrect) {
        const lightEl = document.querySelector('.lamppost .light');

        if (isCorrect) {
            lightEl.style.setProperty('--current-beam-rgb', '119,221,119'); // green
            lightEl.style.setProperty('--current-glow-rgb', '187,255,187'); // green
        } else {
            lightEl.style.setProperty('--current-beam-rgb', '255,85,85'); // red
            lightEl.style.setProperty('--current-glow-rgb', '255,153,153'); // red
        }

        setTimeout(() => {
            lightEl.style.setProperty('--current-beam-rgb', '255,194,84'); // base
            lightEl.style.setProperty('--current-glow-rgb', '255,222,156'); // base
        }, 1000);
    }
});