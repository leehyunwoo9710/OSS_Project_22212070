// 1. 요소 가져오기
const loginBtn = document.getElementById('login-btn');
const logoutBtn = document.getElementById('logout-btn');
const profileBtn = document.getElementById('profile-btn');
const welcomeMsg = document.getElementById('welcome-msg');
const authModal = document.getElementById('auth-modal');
const closeModal = document.getElementById('close-modal');
const toggleAuthMode = document.getElementById('toggle-auth-mode');
const modalTitle = document.getElementById('modal-title');
const submitAuthBtn = document.getElementById('submit-auth-btn');
const usernameInput = document.getElementById('username-input');
const emailInput = document.getElementById('email-input');

const searchInput = document.getElementById('search-input');
const searchBtn = document.getElementById('search-btn');
const cameraBtn = document.getElementById('camera-btn');
const resultArea = document.getElementById('search-result-area');
const cartDisplayArea = document.getElementById('cart-display-area');
const cartList = document.getElementById('cart-list');

const recommendArea = document.getElementById('recommend-display-area');
const recommendBtn = document.getElementById('get-recommendation-btn');
const recommendResult = document.getElementById('recommend-result');
const customSymptomInput = document.getElementById('symptom-custom-input');

// 2. 상태 및 데이터 관리
let currentUser = null;
const userCarts = {}; 
let isLoginMode = true;

const mockDatabase = {
    "타이레놀": { efficacy: "두통, 해열", usage: "1일 최대 8정", warning: "음주 후 복용 시 간 손상 위험" },
    "아스피린": { efficacy: "진통, 혈전 예방", usage: "식후 복용", warning: "위장 출혈 위험" },
    "훼스탈": { efficacy: "소화불량, 과식", usage: "식후 복용", warning: "장기 복용 시 전문의 상의" }
};

const symptomData = {
    "두통": "타이레놀", "치통": "타이레놀", "근육통": "아스피린",
    "발열": "타이레놀", "콧물": "콜대원 (가상)", "기침": "판피린 (가상)",
    "소화불량": "훼스탈"
};

// 3. 계정 기능
loginBtn.addEventListener('click', () => authModal.classList.remove('hidden'));
closeModal.addEventListener('click', () => authModal.classList.add('hidden'));

toggleAuthMode.addEventListener('click', () => {
    isLoginMode = !isLoginMode;
    modalTitle.innerText = isLoginMode ? "로그인" : "회원가입";
    submitAuthBtn.innerText = isLoginMode ? "로그인 하기" : "가입 완료하기";
    toggleAuthMode.innerHTML = isLoginMode ? "계정이 없으신가요? <b>회원가입</b>" : "계정이 있으신가요? <b>로그인</b>";
    emailInput.classList.toggle('hidden', isLoginMode);
});

submitAuthBtn.addEventListener('click', () => {
    const username = usernameInput.value.trim();
    const email = emailInput.value.trim();
    if (!username) return alert("아이디를 입력하세요!");
    if (!isLoginMode && !email) return alert("이메일을 입력하세요!");

    currentUser = username;
    if (!userCarts[currentUser]) {
        userCarts[currentUser] = { items: [], email: email || "미등록" };
    }

    alert(`환영합니다, ${currentUser}님!`);
    authModal.classList.add('hidden');
    loginBtn.classList.add('hidden');
    welcomeMsg.innerText = `${currentUser}님`;
    welcomeMsg.classList.remove('hidden');
    profileBtn.classList.remove('hidden');
    logoutBtn.classList.remove('hidden');
});

logoutBtn.addEventListener('click', () => {
    currentUser = null;
    loginBtn.classList.remove('hidden');
    welcomeMsg.classList.add('hidden');
    profileBtn.classList.add('hidden');
    logoutBtn.classList.add('hidden');
    cartDisplayArea.classList.add('hidden');
    recommendArea.classList.add('hidden');
});

// 4. 검색 및 카메라 기능
searchBtn.addEventListener('click', () => {
    const query = searchInput.value.trim();
    const data = mockDatabase[query];
    resultArea.classList.remove('hidden');
    if (data) {
        resultArea.innerHTML = `
            <div class="result-card">
                <h3>💊 ${query}</h3>
                <p><b>효능:</b> ${data.efficacy}</p>
                <p class="warning-text">⚠️ 주의: ${data.warning}</p>
                <button class="btn-primary" onclick="addToCart('${query}')">➕ 리스트 담기</button>
            </div>`;
    } else {
        resultArea.innerHTML = `<div class="result-card"><h3>검색 결과 없음</h3></div>`;
    }
});

cameraBtn.addEventListener('click', () => {
    alert("스캔 중...");
    setTimeout(() => { searchInput.value = "타이레놀"; alert("스캔 완료!"); }, 1000);
});

// 5. 복약 리스트 기능
window.addToCart = function(name) {
    if (!currentUser) return alert("로그인 후 이용 가능합니다.");
    if (userCarts[currentUser].items.includes(name)) return alert("이미 담긴 약입니다.");
    userCarts[currentUser].items.push(name);
    alert(`${name} 추가 완료!`);
    if (!cartDisplayArea.classList.contains('hidden')) renderCart();
};

window.removeFromCart = function(name) {
    userCarts[currentUser].items = userCarts[currentUser].items.filter(m => m !== name);
    renderCart();
};

function renderCart() {
    cartList.innerHTML = "";
    const items = userCarts[currentUser].items;
    if (items.length === 0) cartList.innerHTML = "<li>비어있음</li>";
    items.forEach(item => {
        cartList.innerHTML += `
            <li style="display:flex; justify-content:space-between; padding:10px; border-bottom:1px solid #eee;">
                <span>💊 ${item}</span>
                <button onclick="removeFromCart('${item}')" style="background:#ff4757; color:white; padding:5px;">삭제</button>
            </li>`;
    });
}

document.getElementById('menu-cart').addEventListener('click', () => {
    if (!currentUser) return alert("로그인 하세요.");
    cartDisplayArea.classList.toggle('hidden');
    recommendArea.classList.add('hidden');
    renderCart();
});

// 6. 증상 추천 기능 (체크박스 + 직접 입력 통합)
document.getElementById('menu-recommend').addEventListener('click', () => {
    if (!currentUser) return alert("로그인 하세요.");
    recommendArea.classList.toggle('hidden');
    cartDisplayArea.classList.add('hidden');
});

recommendBtn.addEventListener('click', () => {
    // 1. 체크박스에서 선택된 증상들
    const checkedSymptoms = Array.from(document.querySelectorAll('#symptom-options input:checked')).map(cb => cb.value);
    
    // 2. 직접 입력창에서 가져온 증상들 (콤마로 구분)
    const typedSymptoms = customSymptomInput.value.split(',').map(s => s.trim()).filter(s => s !== "");
    
    // 3. 두 리스트 합치기
    const allSymptoms = [...new Set([...checkedSymptoms, ...typedSymptoms])];

    if (allSymptoms.length === 0) return alert("증상을 선택하거나 입력해주세요.");

    // 추천 로직: 매핑된 데이터가 있으면 가져오고 없으면 '상담 필요'로 처리
    const recommended = [...new Set(allSymptoms.map(s => symptomData[s] || "약사 상담 필요"))];
    
    recommendResult.classList.remove('hidden');
    recommendResult.innerHTML = `<div class="disclaimer-box">⚠️ 면책 조항: 본 정보는 참고용이며 전문의와 상의하십시오.</div>`;
    
    recommended.forEach(med => {
        if (med === "약사 상담 필요") {
            recommendResult.innerHTML += `
                <div class="recommend-card" style="border-left-color: #94a3b8;">
                    <span style="font-weight:bold; color: #64748b;">👨‍⚕️ 특정 증상은 전문가와 상담하세요</span>
                </div>`;
        } else {
            recommendResult.innerHTML += `
                <div class="recommend-card">
                    <span style="font-weight:bold;">💊 ${med}</span>
                    <button class="header-btn" style="background:#007bff; margin-left:10px;" onclick="addToCart('${med}')">담기</button>
                </div>`;
        }
    });

    // 입력창 초기화
    customSymptomInput.value = "";
    document.querySelectorAll('#symptom-options input:checked').forEach(cb => cb.checked = false);
});