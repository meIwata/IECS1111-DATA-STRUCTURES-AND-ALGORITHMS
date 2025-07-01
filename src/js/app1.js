const minutesInput = document.getElementById('minutes');
const startBtn = document.getElementById('startBtn');
const pauseBtn = document.getElementById('pauseBtn');
const resetBtn = document.getElementById('resetBtn');
const timeDisplay = document.getElementById('timeDisplay');
const statusText = document.getElementById('statusText');
const timerFace = document.querySelector('.timer-face');

let timer;
let totalSeconds = 0;
let isRunning = false;

const cuteMessages = {
    ready: '準備開始專注時間！ ✨',
    running: '專注中... 加油！ 💪',
    paused: '暫停休息一下 😌',
    finished: '太棒了！完成了！ 🎉'
};

function updateDisplay() {
    const minutes = Math.floor(totalSeconds / 60);
    const seconds = totalSeconds % 60;
    timeDisplay.textContent = `${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`;
}

function playAlertSound() {
    const audioContext = new (window.AudioContext || window.webkitAudioContext)();
    const oscillator = audioContext.createOscillator();
    const gainNode = audioContext.createGain();

    oscillator.connect(gainNode);
    gainNode.connect(audioContext.destination);

    oscillator.frequency.setValueAtTime(800, audioContext.currentTime);
    oscillator.frequency.setValueAtTime(600, audioContext.currentTime + 0.1);
    oscillator.frequency.setValueAtTime(800, audioContext.currentTime + 0.2);

    gainNode.gain.setValueAtTime(0.3, audioContext.currentTime);
    gainNode.gain.exponentialRampToValueAtTime(0.01, audioContext.currentTime + 0.3);

    oscillator.start(audioContext.currentTime);
    oscillator.stop(audioContext.currentTime + 0.3);
}

function startTimer() {
    if (!isRunning) {
        totalSeconds = parseInt(minutesInput.value) * 60;
        isRunning = true;
        timerFace.classList.add('running');
        statusText.textContent = cuteMessages.running;

        timer = setInterval(() => {
            totalSeconds--;
            updateDisplay();

            if (totalSeconds <= 0) {
                clearInterval(timer);
                isRunning = false;
                timerFace.classList.remove('running');
                timerFace.classList.add('finished');
                statusText.textContent = cuteMessages.finished;

                // 播放提示音效
                for (let i = 0; i < 3; i++) {
                    setTimeout(() => playAlertSound(), i * 500);
                }

                setTimeout(() => {
                    timerFace.classList.remove('finished');
                    resetTimer();
                }, 3000);
            }
        }, 1000);
    }
}

function pauseTimer() {
    if (isRunning) {
        clearInterval(timer);
        isRunning = false;
        timerFace.classList.remove('running');
        statusText.textContent = cuteMessages.paused;
    }
}

function resetTimer() {
    clearInterval(timer);
    isRunning = false;
    totalSeconds = parseInt(minutesInput.value) * 60;
    timerFace.classList.remove('running', 'finished');
    statusText.textContent = cuteMessages.ready;
    updateDisplay();
}

startBtn.onclick = startTimer;
pauseBtn.onclick = pauseTimer;
resetBtn.onclick = resetTimer;

minutesInput.onchange = () => {
    if (!isRunning) {
        resetTimer();
    }
};

resetTimer();