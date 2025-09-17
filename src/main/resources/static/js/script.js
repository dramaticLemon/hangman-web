const letters = 'A B C D E F G H I J K L M N O P Q R S T U V W X Y Z'.split(' ');
const lettersContainer = document.getElementById('letters');
const wordEl = document.getElementById('word');
const hangmanEl = document.getElementById('hangman');


letters.forEach(letter => {
    const btn = document.createElement('button');
    btn.innerText = letter;
    btn.className = 'letter-btn';
    btn.onclick = () => guessLetter(letter, btn);
    lettersContainer.appendChild(btn);
});

async function guessLetter(letter, btn) {
    btn.disabled = true;
    try {
        const response = await fetch(`/guess?letter=${encodeURIComponent(letter)}`);
        if (!response.ok) throw new Error('Ошибка при запросе');

        const result = await response.json();

        wordEl.innerText = result.currentState;
        hangmanEl.src = `/images/${result.mistakes}.png`;

        if (result.status === 'WIN') {
            alert('🎉 Вы выиграли!');
            disableAllButtons();
        }
        if (result.status === 'LOSE') {
            alert(`💀 Вы проиграли! Слово было: ${result.word}`);
            disableAllButtons();
        }
    } catch (e) {
        console.error('Ошибка:', e);
        alert('Произошла ошибка, попробуйте ещё раз');
    }
}

function disableAllButtons() {
    document.querySelectorAll('.letter-btn').forEach(btn => btn.disabled = true);
}

window.onload = async () => {
    try {
        const response = await fetch('/start-game');
        const result = await response.json();
        wordEl.innerText = result.currentState;
    } catch (e) {
        console.error('Ошибка при инициализации игры:', e);
    }
};
