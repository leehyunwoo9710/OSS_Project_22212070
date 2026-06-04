const API_BASE_URL = "http://localhost:8080";

const loginBtn = document.getElementById("login-btn");
const logoutBtn = document.getElementById("logout-btn");
const profileBtn = document.getElementById("profile-btn");
const welcomeMsg = document.getElementById("welcome-msg");
const authModal = document.getElementById("auth-modal");
const closeModal = document.getElementById("close-modal");
const toggleAuthMode = document.getElementById("toggle-auth-mode");
const modalTitle = document.getElementById("modal-title");
const submitAuthBtn = document.getElementById("submit-auth-btn");
const usernameInput = document.getElementById("username-input");
const emailInput = document.getElementById("email-input");
const passwordInput = document.getElementById("password-input");
const authErrorMsg = document.getElementById("auth-error-msg");

const profileModal = document.getElementById("profile-modal");
const closeProfileModal = document.getElementById("close-profile-modal");
const profileUsername = document.getElementById("profile-username");
const profileEmail = document.getElementById("profile-email");
const profilePassword = document.getElementById("profile-password");
const saveProfileBtn = document.getElementById("save-profile-btn");

const searchInput = document.getElementById("search-input");
const searchBtn = document.getElementById("search-btn");
const cameraBtn = document.getElementById("camera-btn");
const cameraInput = document.getElementById("camera-input");
const resultArea = document.getElementById("search-result-area");
const cartDisplayArea = document.getElementById("cart-display-area");
const cartList = document.getElementById("cart-list");

const recommendArea = document.getElementById("recommend-display-area");
const recommendBtn = document.getElementById("get-recommendation-btn");
const recommendResult = document.getElementById("recommend-result");
const customSymptomInput = document.getElementById("symptom-custom-input");

let currentUser = null;
let isLoginMode = true;

loginBtn.addEventListener("click", () => {
    authModal.classList.remove("hidden");
    authErrorMsg.classList.add("hidden");
});
closeModal.addEventListener("click", () => authModal.classList.add("hidden"));

toggleAuthMode.addEventListener("click", () => {
    isLoginMode = !isLoginMode;
    authErrorMsg.classList.add("hidden");
    modalTitle.innerText = isLoginMode ? "로그인" : "회원가입";
    submitAuthBtn.innerText = isLoginMode ? "계속하기" : "계정 만들기";
    toggleAuthMode.innerHTML = isLoginMode
        ? "계정이 없으신가요? <b>회원가입</b>"
        : "이미 계정이 있으신가요? <b>로그인</b>";
    emailInput.classList.toggle("hidden", isLoginMode);
});

submitAuthBtn.addEventListener("click", async () => {
    const username = usernameInput.value.trim();
    const email = emailInput.value.trim();
    const password = passwordInput.value.trim();

    if (!username) {
        authErrorMsg.innerText = "아이디를 입력해주세요.";
        authErrorMsg.classList.remove("hidden");
        return;
    }

    if (!password) {
        authErrorMsg.innerText = "비밀번호를 입력해주세요.";
        authErrorMsg.classList.remove("hidden");
        return;
    }

    if (!isLoginMode && !email) {
        authErrorMsg.innerText = "이메일을 입력해주세요.";
        authErrorMsg.classList.remove("hidden");
        return;
    }

    try {
        const endpoint = isLoginMode ? "/api/auth/login" : "/api/auth/signup";
        const response = await fetch(`${API_BASE_URL}${endpoint}`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ username, email: isLoginMode ? null : email, password })
        });

        if (!response.ok) {
            throw new Error("서버 연동에 실패했습니다.");
        }

        const data = await response.json();

        if (!data.success) {
            authErrorMsg.innerText = data.message;
            authErrorMsg.classList.remove("hidden");
            return;
        }

        // 회원가입 성공 시 자동 로그인 되므로 메시지 창만 가림
        authErrorMsg.classList.add("hidden");

        currentUser = username;
        authModal.classList.add("hidden");
        loginBtn.classList.add("hidden");
        welcomeMsg.innerText = `${currentUser}`;
        welcomeMsg.classList.remove("hidden");
        profileBtn.classList.remove("hidden");
        logoutBtn.classList.remove("hidden");
    } catch (error) {
        authErrorMsg.innerText = error.message;
        authErrorMsg.classList.remove("hidden");
    }
});

logoutBtn.addEventListener("click", () => {
    currentUser = null;
    loginBtn.classList.remove("hidden");
    welcomeMsg.classList.add("hidden");
    profileBtn.classList.add("hidden");
    logoutBtn.classList.add("hidden");
    cartDisplayArea.classList.add("hidden");
    recommendArea.classList.add("hidden");
});

closeProfileModal.addEventListener("click", () => profileModal.classList.add("hidden"));

profileBtn.addEventListener("click", async () => {
    if (!currentUser) return;
    
    try {
        const response = await fetch(`${API_BASE_URL}/api/auth/profile`, {
            headers: { "X-Username": currentUser }
        });
        
        if (!response.ok) throw new Error("프로필을 불러오는데 실패했습니다.");
        
        const data = await response.json();
        profileUsername.value = data.username;
        profileEmail.value = data.email || "";
        profilePassword.value = data.password;
        
        profileModal.classList.remove("hidden");
    } catch(err) {
        alert(err.message);
    }
});

saveProfileBtn.addEventListener("click", async () => {
    if (!currentUser) return;
    
    const email = profileEmail.value.trim();
    const password = profilePassword.value.trim();
    
    if (!password) {
        alert("비밀번호는 비워둘 수 없습니다.");
        return;
    }
    
    try {
        const response = await fetch(`${API_BASE_URL}/api/auth/profile`, {
            method: "PUT",
            headers: { 
                "Content-Type": "application/json",
                "X-Username": currentUser 
            },
            body: JSON.stringify({ email, password })
        });
        
        if (!response.ok) throw new Error("프로필 저장에 실패했습니다.");
        
        const data = await response.json();
        if (data.success) {
            alert("프로필 수정 완료");
            profileModal.classList.add("hidden");
        } else {
            alert(data.message);
        }
    } catch(err) {
        alert(err.message);
    }
});

searchBtn.addEventListener("click", async () => {
    const query = searchInput.value.trim();

    if (!query) {
        alert("약 이름을 입력해주세요.");
        return;
    }

    await renderSearchResults(query);
});

cameraBtn.addEventListener("click", () => {
    alert("약 사진을 정확히 정면에서 촬영해주세요.");
    cameraInput.click();
});

cameraInput.addEventListener("change", async (e) => {
    const file = e.target.files[0];
    if (!file) return;

    resultArea.classList.remove("hidden");
    resultArea.innerHTML = `<div class="result-card"><h3>카메라 이미지 분석 중...</h3><p>잠시만 기다려주세요.</p></div>`;

    const formData = new FormData();
    formData.append("image", file);

    try {
        const response = await fetch(`${API_BASE_URL}/api/ocr/scan`, {
            method: "POST",
            body: formData
        });

        if (!response.ok) {
            const errData = await response.json().catch(() => ({}));
            throw new Error(errData.error || "OCR 분석에 실패했습니다.");
        }

        const data = await response.json();
        
        if (!data.text || data.text === "인식된 텍스트가 없습니다.") {
            throw new Error("이미지에서 텍스트를 인식하지 못했습니다. 다시 촬영해주세요.");
        }

        // 검색창에 결과 입력 후 바로 검색 실행
        searchInput.value = data.text;
        await renderSearchResults(data.text);
        
    } catch (error) {
        resultArea.innerHTML = `<div class="result-card" style="border-left-color:#ef4444;">
            <h3>분석 실패</h3>
            <p>${escapeHtml(error.message)}</p>
        </div>`;
    } finally {
        // 같은 파일을 다시 선택해도 이벤트가 발생하도록 value 초기화
        cameraInput.value = "";
    }
});

async function fetchDrugItems(query, numOfRows = 10, isChild = false) {
    const url = new URL(`${API_BASE_URL}/api/drug/search`);
    url.searchParams.set("itemName", query);
    url.searchParams.set("numOfRows", String(numOfRows));
    url.searchParams.set("isChild", String(isChild));

    const response = await fetch(url);
    if (!response.ok) {
        throw new Error("약 정보를 불러오는데 실패했습니다.");
    }

    const data = await response.json();
    return normalizeItems(data?.body?.items);
}

function normalizeItems(items) {
    if (!items) {
        return [];
    }

    if (Array.isArray(items)) {
        return items;
    }

    if (Array.isArray(items.item)) {
        return items.item;
    }

    return [items.item ?? items].filter(Boolean);
}

async function renderSearchResults(query) {
    resultArea.classList.remove("hidden");
    resultArea.innerHTML = `<div class="result-card"><h3>검색 중...</h3></div>`;

    try {
        const isChildSearch = document.getElementById("child-search-checkbox")?.checked;
        let items = await fetchDrugItems(query, 50, isChildSearch); // 백엔드에서 필터링해서 가져옴

        if (items.length === 0) {
            resultArea.innerHTML = `<div class="result-card"><h3>조건에 맞는 결과가 없습니다</h3></div>`;
            return;
        }

        resultArea.innerHTML = items.map(renderDrugCard).join("");
    } catch (error) {
        resultArea.innerHTML = `<div class="result-card"><h3>검색 실패</h3><p>${escapeHtml(error.message)}</p></div>`;
    }
}

function renderDrugCard(item) {
    const encoded = encodeURIComponent(JSON.stringify(item));

    return `
        <div class="result-card">
            <h3>${escapeHtml(item.itemName ?? "알 수 없는 약")}</h3>
            <p><b>효능:</b> ${escapeHtml(item.efcyQesitm ?? "정보 없음")}</p>
            <p><b>사용법:</b> ${escapeHtml(item.useMethodQesitm ?? "정보 없음")}</p>
            <p class="warning-text"><b>주의사항:</b> ${escapeHtml(item.atpnQesitm ?? "정보 없음")}</p>
            <button class="btn-primary" onclick="addToCart('${encoded}')">목록에 추가</button>
        </div>
    `;
}

window.addToCart = async function(encodedItem) {
    if (!currentUser) {
        alert("먼저 로그인해주세요.");
        return;
    }

    const item = JSON.parse(decodeURIComponent(encodedItem));

    try {
        const response = await fetch(`${API_BASE_URL}/api/cart/add`, {
            method: "POST",
            headers: { 
                "Content-Type": "application/json",
                "X-Username": currentUser
            },
            body: JSON.stringify(item)
        });

        if (!response.ok) {
            throw new Error("항목 추가에 실패했습니다.");
        }

        alert(`${item.itemName ?? "약"}이(가) 추가되었습니다.`);

        if (!cartDisplayArea.classList.contains("hidden")) {
            await renderCart();
        }
    } catch (error) {
        alert(error.message);
    }
};

window.removeFromCart = async function(id) {
    try {
        const response = await fetch(`${API_BASE_URL}/api/cart/${id}`, { 
            method: "DELETE",
            headers: { "X-Username": currentUser }
        });

        if (!response.ok) {
            throw new Error("항목 삭제에 실패했습니다.");
        }

        await renderCart();
    } catch (error) {
        alert(error.message);
    }
};

async function renderCart() {
    cartList.innerHTML = `<li>로딩 중...</li>`;

    try {
        const response = await fetch(`${API_BASE_URL}/api/cart/list`, {
            headers: { "X-Username": currentUser }
        });

        if (!response.ok) {
            throw new Error("복약 리스트를 불러오는데 실패했습니다.");
        }

        const items = await response.json();

        if (items.length === 0) {
            cartList.innerHTML = "<li>비어있음</li>";
            return;
        }

        cartList.innerHTML = items.map(item => `
            <li style="display:flex; justify-content:space-between; gap:12px; padding:10px; border-bottom:1px solid #eee;">
                <span>${escapeHtml(item.itemName ?? "알 수 없는 약")}</span>
                <button onclick="removeFromCart(${item.id})" style="background:#ff4757; color:white; padding:5px;">삭제</button>
            </li>
        `).join("");
    } catch (error) {
        cartList.innerHTML = `<li>${escapeHtml(error.message)}</li>`;
    }
}

document.getElementById("menu-cart").addEventListener("click", async () => {
    if (!currentUser) {
        alert("먼저 로그인해주세요.");
        return;
    }

    cartDisplayArea.classList.toggle("hidden");
    recommendArea.classList.add("hidden");

    if (!cartDisplayArea.classList.contains("hidden")) {
        await renderCart();
    }
});

document.getElementById("menu-recommend").addEventListener("click", () => {
    if (!currentUser) {
        alert("먼저 로그인해주세요.");
        return;
    }

    recommendArea.classList.toggle("hidden");
    cartDisplayArea.classList.add("hidden");
});

async function fetchDrugBySymptom(symptom, numOfRows = 1, isChild = false) {
    const url = new URL(`${API_BASE_URL}/api/drug/recommend`);
    url.searchParams.set("symptom", symptom);
    url.searchParams.set("numOfRows", String(numOfRows));
    url.searchParams.set("isChild", String(isChild));

    const response = await fetch(url);
    if (!response.ok) {
        throw new Error("약 정보를 불러오는데 실패했습니다.");
    }

    const data = await response.json();
    return normalizeItems(data?.body?.items);
}

recommendBtn.addEventListener("click", async () => {
    const checkedSymptoms = Array.from(document.querySelectorAll("#symptom-options input:checked"))
        .map(checkbox => checkbox.value);
    const typedSymptoms = customSymptomInput.value
        .split(",")
        .map(value => value.trim())
        .filter(Boolean);
    const symptoms = [...new Set([...checkedSymptoms, ...typedSymptoms])];

    if (symptoms.length === 0) {
        alert("증상을 선택하거나 입력해주세요.");
        return;
    }

    recommendResult.classList.remove("hidden");
    recommendResult.innerHTML = `<div class="disclaimer-box">추천은 참고용입니다. 정확한 약학적 안내는 전문가에게 문의하세요.</div><div class="result-card"><h3>검색 중...</h3></div>`;

    let resultsHtml = `<div class="disclaimer-box">추천은 참고용입니다. 정확한 약학적 안내는 전문가에게 문의하세요.</div>`;

    try {
        const isChildRecommend = document.getElementById("child-recommend-checkbox")?.checked;
        
        // 첫 번째 증상을 기준으로 여러 개의 약을 먼저 가져옵니다 (최대 50개) - 백엔드에서 필터링 적용됨
        let items = await fetchDrugBySymptom(symptoms[0], 50, isChildRecommend);
        
        let filteredItems = items;
        
        // 복수 증상인 경우, 나머지 증상들도 효능에 포함되어 있는지 필터링
        if (symptoms.length > 1) {
            filteredItems = items.filter(item => {
                const efcy = item.efcyQesitm || "";
                for (let i = 1; i < symptoms.length; i++) {
                    if (!efcy.includes(symptoms[i])) {
                        return false;
                    }
                }
                return true;
            });
        }

        if (filteredItems.length === 0) {
            resultsHtml += `
                <div class="recommend-card" style="border-left-color:#94a3b8;">
                    <span><b>${escapeHtml(symptoms.join(", "))}</b>: 해당 증상을 모두 만족하는 약이 없습니다.</span>
                </div>
            `;
        } else {
            // 단일 증상이든 복수 증상이든 필터링된 약들 중 하나를 무작위로 선택
            const randomIndex = Math.floor(Math.random() * filteredItems.length);
            const selectedItem = filteredItems[randomIndex];
            const encoded = encodeURIComponent(JSON.stringify(selectedItem));
            
            resultsHtml += `
                <div class="recommend-card">
                    <span><b>${escapeHtml(symptoms.join(", "))}</b>: ${escapeHtml(selectedItem.itemName)}</span>
                    <button class="header-btn" style="background:#007bff; margin-left:10px;" onclick="addToCart('${encoded}')">추가</button>
                </div>
            `;
        }
    } catch (error) {
        resultsHtml += `
            <div class="recommend-card" style="border-left-color:#ef4444;">
                <span><b>${escapeHtml(symptoms.join(", "))}</b>: ${escapeHtml(error.message)}</span>
            </div>
        `;
    }

    recommendResult.innerHTML = resultsHtml;
    customSymptomInput.value = "";
    document.querySelectorAll("#symptom-options input:checked").forEach(checkbox => {
        checkbox.checked = false;
    });
});

function escapeHtml(value) {
    return String(value)
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#039;");
}
