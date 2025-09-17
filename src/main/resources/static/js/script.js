let initialTries = 0;

// page element
const letters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ'.split('');
const lettersContainer = document.getElementById('letters');
const wordEl = document.getElementById('word');
const hangmanEl = document.getElementById('hangman');

// create button for all letter
letters.forEach(letter => {
    const btn = document.createElement('button');
    btn.type = 'button';
    btn.innerText = letter;
    btn.className = 'letter-btn';
    btn.onclick = () => guessLetter(letter, btn);
    lettersContainer.appendChild(btn);
});

// run a new game
async function startGame() {
    try {
        const response = await fetch('/api/hangman/start', { method: 'POST' });
        if (!response.ok) throw new Error('Не удалось начать игру');

        const result = await response.json();

        // save started count try
        initialTries = result.remainingTries;

        // display current state word (ex. "_ _ _ _ _ _")
        wordEl.innerText = result.currentState;

        hangmanEl.src = `/images/0.png`;

        enableAllButtons();
    } catch (e) {
        console.error('Ошибка при старте игры:', e);
        alert('Не удалось запустить игру');
    }
}

async function guessLetter(letter, btn) {
    btn.disabled = true;

    try {
        const response = await fetch(`/api/hangman/guess?letter=${encodeURIComponent(letter)}`, {
            method: 'POST'
        });

        if (!response.ok) throw new Error('Ошибка при запросе');

        const result = await response.json();

        wordEl.innerText = result.currentState;
        const mistakes = initialTries - result.remainingTries;
        alert(mistakes);
        hangmanEl.src = `/images/${mistakes}.png`;

        if (result.status === 'WIN') {
            showGameOverMessage('🎉 You Win!');
        } else if (result.status === 'LOST') {
            showGameOverMessage(`💀 You Lose! Слово было: ${result.word}`);
        }

    } catch (e) {
        console.error('Ошибка:', e);
        alert('Произошла ошибка, попробуйте ещё раз');
    }
}


function disableAllButtons() {
    document.querySelectorAll('.letter-btn').forEach(btn => btn.disabled = true);
}

function enableAllButtons() {
    document.querySelectorAll('.letter-btn').forEach(btn => btn.disabled = false);
}

function showGameOverMessage(message) {
    disableAllButtons();

    const gameOverEl = document.getElementById('game-over');
    const messageEl = document.getElementById('game-over-message');

    messageEl.innerText = message;
    gameOverEl.style.display = 'block';
}

function restartGame() {
    document.getElementById('game-over').style.display = 'none';
    startGame(); // вызываем старт новой игры
}

window.onload = startGame;
