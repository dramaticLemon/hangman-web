let initialTries = 0;

window.addEventListener('DOMContentLoaded', () => {
    // EN UA
    const englishLetters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ'.split('');
    const uaLetters = 'АБВГҐДЕЄЖЗИІЇЙКЛМНОПРСТУФХЦЧШЩЬЮЯ'.split('');
    let currentLetters = englishLetters; // current

    const lettersContainer = document.getElementById('letters');
    const wordEl = document.getElementById('word');
    const hangmanEl = document.getElementById('hangman');
    const gameOverEl = document.getElementById('game-over');
    const gameOverMessage = document.getElementById('game-over-message');
    const languageBtn = document.querySelector('.language-btn');

    // button render
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

    languageBtn.addEventListener('click', () => {
        if (currentLetters === englishLetters) {
            currentLetters = uaLetters;
            languageBtn.innerText = 'EN';
        } else {
            currentLetters = englishLetters;
            languageBtn.innerText = 'UA';
        }
        renderLetterButtons(currentLetters);
    });

    // restart game
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

    function triggerConfetti() {
        const container = document.getElementById('confetti-container');
        const confettiCount = 200;

        for (let i = 0; i < confettiCount; i++) {
            const confetti = document.createElement('div');
            confetti.className = 'confetti';
            confetti.style.backgroundColor = `hsl(${Math.random() * 360}, 100%, 50%)`;
            confetti.style.left = Math.random() * 100 + 'vw';
            const size = 5 + Math.random() * 12;
            confetti.style.width = confetti.style.height = size + 'px';
            confetti.style.setProperty('--x-move', `${Math.random() * 100 - 50}px`);
            confetti.style.setProperty('--fall-duration', `${2 + Math.random() * 2}s`);
            confetti.style.setProperty('--spin-duration', `${1 + Math.random()}s`);

            container.appendChild(confetti);
            confetti.addEventListener('animationend', () => confetti.remove());
        }
    }

    // logic guess letter
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
                showGameOverMessage('🎉You WON!');
                triggerConfetti();
            } else if (result.status === 'LOST') {
                showGameOverMessage(`💀 You lose! Word: ${result.word}`);
            }
        } catch (e) {
            console.error('Error:', e);
        }
    }

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