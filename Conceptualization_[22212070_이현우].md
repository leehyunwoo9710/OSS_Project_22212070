# 1. Conceptualization
<img width="1024" height="559" alt="image" src="https://github.com/user-attachments/assets/d760c9ac-d095-46f3-8b72-759ce7fc01a5" />


|||
| --- | --- |
| **Title** | **Medicine Helper** |
| **Student No** | **22212070** |
| **Name** | **이현우** |
| **E-mail** | **lhw031103@yu.ac.kr** |

---

## Revision History

| Revision date | Version # | Description | Author |
|---|---|---|---|
| 03/27/2026 | 1.00 | First draft | 이현우 |

---

## Contents

1. [Business purpose](#1-business-purpose)
2. [System context diagram](#2-system-context-diagram)
3. [Use case list](#3-use-case-list)
4. [Concept of operation](#4-concept-of-operation)
5. [Problem statement](#5-problem-statement)
6. [Glossary](#6-glossary)
7. [References](#7-references)

---

## 1. Business purpose

### **1-1 Background:**

* 최근 2025년 통계에 따르면 국내 의약품 부작용 보고가 27만건을 넘어섰으며, 특히 10대와 20대의 의약품 중독 환자가 급증하고 있다.
* 또한 초고령 사회 진입으로 10종 이상의 약물을 복용하는 노인이 170만명에 달하는 상황에서, 사용자가 복용하려는 약의 효능뿐만 아니라 부작용 및 오남용 위험성을 사전에 인지할 수 있도록 돕는 디지털 헬스케어 솔루션이 요구된다.
* 최근 발생한 종각역 택시 사고에서는 자동차 2대와 횡단보도에서 신호 대기 중이던 보행자들을 잇따라 들이받는 추돌사고를 일으켜 1명이 사망하고 14명이 부상을 입은 사건이 발생했다. 이 사건의 원인으로 택시운전사인 70대후반 남성에게 감기약 관련 모르핀 양성 반응이 검출된 것으로 알려졌다.
* 이와 같이 고령의 신체상태와 특정 약이 결합하면 졸음 반응 등 문제를 일으킬 가능성 또한 다분하다고 여겨진다.
  <img width="1140" height="403" alt="article" src="https://github.com/user-attachments/assets/f0acb0e1-5563-43f2-a358-a9eee9d8441b" />
위의 사진에서 보이듯이 노인들의 약에 관한 정보가 적음을 알 수 있다

### **1-2 Goals:**

* 본 프로젝트의 목표는 앞서 기술했듯이 노인을 포함한 대한민국 구성원들에게 약에 대한 정확한 정보 제공과 부작용 가능성 같은 기능들을 앞세워 약에 대한 관심 상승과 이로 인해 효율적인 약 복용을 도울 수 있게 해준다.

---

## 2. System context diagram

<img width="1451" height="720" alt="diagramimg" src="https://github.com/user-attachments/assets/323a7d91-8a77-4cd9-b30e-ce5baa50e095" />

**User->System**
 -login
 -register
 -logout
 -view profile
 -medicine information
 -personal medication info
 -record user state
 -e-mail notification setup

**System->Server**
 -request login
 -request logout
 -request register

**Server->System**
 -provide e-mail api
 -provide medicine api

**System->User**
 -provide all information
 -recommend medicine

---

## 3. Use case list

### 1) Login
| 항목 | 내용 |
|---|---|
| Actor | User |
| Description | 사용자가 로그인을 한다. |

### 2) register
| 항목 | 내용 |
|---|---|
| Actor | User |
| Description | 사용자가 회원가입을 한다. |

### 3) logout
| 항목 | 내용 |
|---|---|
| Actor | User |
| Description | 사용자가 로그아웃을 한다. |

### 4) view profile
| 항목 | 내용 |
|---|---|
| Actor | User |
| Description | 사용자가 자신의 정보를 조회한다. |

### 5) Medicine information
| 항목 | 내용 |
|---|---|
| Actor | User |
| Description | 사용자가 약 정보를 검색한다. |

### 6) personal medication info
| 항목 | 내용 |
|---|---|
| Actor | User |
| Description | 사용자가 자신만의 약 정보를 기록한다 |

### 7) record user state
| 항목 | 내용 |
|---|---|
| Actor | User |
| Description | 사용자가 자신의 아픔 상태를 기록한다. |

### 8) e-mail notification setup
| 항목 | 내용 |
|---|---|
| Actor | User |
| Description | 사용자가 이메일 기반 알람 설정을 한다. |

### 9) provide information
| 항목 | 내용 |
|---|---|
| Actor | System |
| Description | 시스템이 사용자에게 약 관련 정보를 제공한다. |

### 10) recommend medicine
| 항목 | 내용 |
|---|---|
| Actor | System |
| Description | 시스템이 사용자에게 약을 추천한다. |

---

## 4. Concept of operation

### 1) Login
| 항목 | 내용 |
|---|---|
| Purpose | 프로그램을 사용하기 위해 등록된 사용자인지 확인 |
| Approach | 사용자가 앱을 실행 후 로그인 시, ID, PW를 입력 후 로그인을 요청하면 서버에서 회원 정보를 조회 후 로그인 성공/실패 여부 확인한다. |
| Dynamics | 앱 실행 시 로그인할 경우 |
| Goals | 로그인 기능을 구현한다. |

### 2) register
| 항목 | 내용 |
|---|---|
| Purpose | 프로그램을 사용하기 위해 사용자를 등록 |
| Approach | 사용자에게 필요한 정보들을 입력받은 후에 중복되는 ID가 없는지 확인후 서버에 저장한다 |
| Dynamics | 로그인을 위해 회원가입을 하는 경우 |
| Goals | 회원가입 기능을 구현한다 |

### 3) logout
| 항목 | 내용 |
|---|---|
| Purpose | 프로그램을 다 사용했거나 다른 사용자로 로그인 하고 싶은 경우 로그아웃 |
| Approach | 사용자가 로그아웃을 할 수 있게 한다 |
| Dynamics | 로그아웃을 실행할 경우 |
| Goals | 로그아웃 기능을 구현한다. |

### 4) view profile
| 항목 | 내용 |
|---|---|
| Purpose | 사용자가 자신의 정보를 조회할 수 있게 해준다 |
| Approach | 자신의 정보를 확인하거나 정보를 바꿀 수 있다 |
| Dynamics | 사용자가 자신의 정보를 확인하거나 바꾸고 싶은 경우 |
| Goals | 프로필 기능을 구현한다. |

### 5) Medicine information
| 항목 | 내용 |
|---|---|
| Purpose | 약 정보를 검색 |
| Approach | 약에 대한 상세정보를 알기 위해 약을 검색한다 |
| Dynamics | 약 정보 검색하는 경우 |
| Goals | 약을 검색하는 기능을 구현한다 |

### 6) personal medication info
| 항목 | 내용 |
|---|---|
| Purpose | 사용자가 자신만의 약 리스트를 구성 |
| Approach | 사용자가 리스트에 자신이 복용할 약들을 담는다 |
| Dynamics | 사용자가 자신이 복용할 약들의 리스트들을 원할 경우 |
| Goals | 개인만의 장바구니 개념 리스트를 구현한다 |

### 7) record user state
| 항목 | 내용 |
|---|---|
| Purpose | 사용자가 자신의 아픔 상태를 작성해서 약 선택 |
| Approach | 사용자가 자신의 아픔 상태를 작성한다 |
| Dynamics | 자신의 상태에 따라 약을 선택하는 경우 |
| Goals | 검색창에 자신의 아픈 상태를 작성하는 체크리스트 구현한다 |

### 8) e-mail notification setup
| 항목 | 내용 |
|---|---|
| Purpose | 사용자가 약 먹을 시간 알림 설정 |
| Approach | 사용자가 약 먹을 시간이 되면 이메일로 먹을 시간이라고 통보를 받는다 |
| Dynamics | 사용자가 약 먹을 시간을 잊어버리지 않게 알림을 원할 경우 |
| Goals | e-mail을 활용하여 알림기능을 구현한다 |

### 9) provide information
| 항목 | 내용 |
|---|---|
| Purpose | 사용자에게 약 관련 정보 제공 |
| Approach | 사용자가 원하는 약의 상세정보 및 주의사항을 제공한다 |
| Dynamics | 사용자가 약을 검색 할 경우 |
| Goals | 사용자가 검색한 약 정보를 DB로부터 정보를 얻어와서 정보를 제공한다 |

### 10) recommend medicine
| 항목 | 내용 |
|---|---|
| Purpose | 사용자에게 몸 상태에 알맞는 약들을 제안 |
| Approach | 사용자의 몸 상태를 기반으로 그에 알맞는 약들을 제안한다 |
| Dynamics | 사용자가 약을 추천받고 싶을때 |
| Goals | 사용자의 상태를 분석해서 알맞는 약을 추천해주는 기능을 구현한다 |

---

## 5. Problem statement

**problem #1 - Technical problem**
API와 DB관련 기술에 익숙하지 않다. 프로그램이 잘 작동하도록 API와 DB관련 공부를 할 것이고 정보 검토를 확실히 할 것이다.

**problem #2 - Potential Medical Law Violations**
의료법 제27조에 따르면 의료인이 아닌 자가 무면허 의료행위를 하는 것은 금지되어 있어서 프로그램을 의사처럼 “A약을 B약으로 바꾸세요“ 라고 결정을 내리는 것이 아닌 공공데이터에 있는 사실을 보여주는 형태로 구성해야 하고 Recommend medicine에서 사용자의 상태에 따라 ”타이레놀을 드세요“ 라고 추천하는 것이 아닌 상태에 맞는 약들의 리스트를 보여주어야 한다. 또한 이 프로그램은 의학적 진단을 내리는 것이 아닌 공공데이터를 사용자에게 전달하는 ”건강 관리 보조 도구“ 라는 점을 명확히 명시해놓아야 한다.

**problem #3 - Data Accuracy & Liability**
공공데이터의 api정보가 실제 약의 정보와 미세하게 다를 수가 있음. 따라서 본 프로그램은 보조적인 도움 수단일 뿐이며 최종 책임은 사용자에게 있다라는 점을 명시해야 한다.

---

## 6. Glossary

- **Medicine Helper** – 사용자의 약 관련 생활 보조 프로그램
- **리스트** – 사용자가 등록한 약들의 모음
- **서버** – 회원 정보 및 약 정보 등을 저장하기 위한 것

---

## 7. References

1. Gmail API (https://developers.google.com/workspace/gmail/api/reference/rest?hl=ko)
2. Medicine API (https://www.data.go.kr/index.do)
3. 이데일리 기사 “약 안먹고 못버티는데…어떤 약이 위험한지 모르죠” - 2026.01.06
