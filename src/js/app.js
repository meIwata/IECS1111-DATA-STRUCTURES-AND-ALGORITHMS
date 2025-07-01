const folderInput = document.getElementById('folder-input');
const chooseBtn = document.getElementById('choose-btn');
const prevBtn = document.getElementById('prev-btn');
const nextBtn = document.getElementById('next-btn');
const preview = document.getElementById('preview');

let imageFiles = [];
let currentIndex = -1;

// 監聽「選取圖片所在目錄」按鈕
chooseBtn.onclick = () => {
    folderInput.value = ""; // reset
    folderInput.click();
};

// 處理選取的資料夾
folderInput.onchange = (e) => {
    const files = Array.from(e.target.files);
    // 只要圖片檔案
    imageFiles = files.filter(file => /\.(jpg|jpeg|png|gif|bmp)$/i.test(file.name));
    imageFiles.sort((a, b) => a.webkitRelativePath.localeCompare(b.webkitRelativePath));
    if (imageFiles.length > 0) {
        currentIndex = 0;
        showImage();
    } else {
        preview.style.display = "none";
        preview.src = "";
        alert("目錄下沒有圖片檔案！");
    }
};

// 顯示當前圖片
function showImage() {
    if (currentIndex < 0 || currentIndex >= imageFiles.length) return;
    const file = imageFiles[currentIndex];
    const reader = new FileReader();
    reader.onload = e => {
        preview.src = e.target.result;
        preview.style.display = "block";
        // 圖片以原始 1/2 顯示（css已控制最大寬高，不用再縮放）
    };
    reader.readAsDataURL(file);
}

// 上一張
prevBtn.onclick = () => {
    if (imageFiles.length === 0) return;
    currentIndex = (currentIndex - 1 + imageFiles.length) % imageFiles.length;
    showImage();
};

// 下一張
nextBtn.onclick = () => {
    if (imageFiles.length === 0) return;
    currentIndex = (currentIndex + 1) % imageFiles.length;
    showImage();
};