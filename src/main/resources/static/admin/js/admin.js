// ═══════════════════════════════════════════════════════════════
//  admin.js — Core utilities, API layer, Router, DeleteConfirm
//  Shared across ALL admin pages
// ═══════════════════════════════════════════════════════════════

/* ───────────────────────────────────────────────────────────────
   DB CONFIG  ← Set apiBase and token once DB is connected
─────────────────────────────────────────────────────────────── */
const pathPrefixBeforeAdmin = window.location.pathname.split("/admin")[0] || "";
const APP_CONTEXT_PATH =
  document.body?.dataset?.contextPath || pathPrefixBeforeAdmin;

const DB_CONFIG = {
  apiBase: `${APP_CONTEXT_PATH}/admin`,
  token: localStorage.getItem("authToken") || null, // Đọc token đã lưu sau login
};

/* ───────────────────────────────────────────────────────────────
   MOCK DATA  ← Remove this entire block after DB is wired
─────────────────────────────────────────────────────────────── */
const MOCK = {
  orders: [],
  orderDetails: {},
  users: [],
  products: [],
  categories: [],
  reviews: [],
  discounts: [],
};

/* ───────────────────────────────────────────────────────────────
   API LAYER
   Replace each function body with real fetch() when DB ready
─────────────────────────────────────────────────────────────── */
const API = {
  _h() {
    return {
      "Content-Type": "application/json",
      ...(DB_CONFIG.token
        ? { Authorization: `Bearer ${DB_CONFIG.token}` }
        : {}),
    };
  },
  _opts(method = "GET", body = null) {
    return {
      method,
      headers: this._h(),
      credentials: "include",
      ...(body !== null ? { body: JSON.stringify(body) } : {}),
    };
  },

  // ── ORDERS ──────────────────────────────────────────────────
  async getOrders(page = 0) {
    const response = await fetch(
      `${DB_CONFIG.apiBase}/api/orders?page=${page}&size=10`,
      { headers: this._h(), credentials: "include" },
    );
    if (!response.ok) {
      throw new Error("Khong the tai danh sach don hang");
    }
    return response.json();
  },
  async getOrderById(id) {
    const response = await fetch(`${DB_CONFIG.apiBase}/api/orders/${id}`, {
      headers: this._h(),
      credentials: "include",
    });
    if (!response.ok) {
      throw new Error("Khong the tai chi tiet don hang");
    }
    return response.json();
  },
  async updateOrder(id, d) {
    const response = await fetch(`${DB_CONFIG.apiBase}/api/orders/${id}`, {
      ...this._opts("PUT", d),
    });
    if (!response.ok) {
      const errText = await response.text().catch(() => "Khong the cap nhat don hang");
      throw new Error(errText || "Khong the cap nhat don hang");
    }
    return response.text().catch(() => null);
  },
  async deleteOrder(id) {
    const response = await fetch(`${DB_CONFIG.apiBase}/api/orders/${id}`, {
      method: "DELETE",
      headers: this._h(),
      credentials: "include",
    });
    if (!response.ok) {
      throw new Error("Khong the xoa don hang");
    }
  },

  // ── USERS ────────────────────────────────────────────────────
  async getUsers() {
    const response = await fetch(`${DB_CONFIG.apiBase}/user/all`, {
      headers: this._h(),
      credentials: "include",
    });
    if (!response.ok) {
      throw new Error("Khong the tai danh sach user");
    }
    return response.json();
  },
  async createUser(d) {
    const response = await fetch(`${DB_CONFIG.apiBase}/user/add`, {
      ...this._opts("POST", d),
    });
    if (!response.ok) {
      const errorText = await response.text();
      throw new Error(errorText || "Khong the tao user");
    }
    return response.json();
  },
  async updateUser(id, d) {
    const response = await fetch(`${DB_CONFIG.apiBase}/user/${id}`, {
      ...this._opts("PUT", d),
    });
    if (!response.ok) {
      const errorText = await response.text();
      throw new Error(errorText || "Khong the cap nhat user");
    }
    return response.json();
  },
  async deleteUser(id) {
    const response = await fetch(`${DB_CONFIG.apiBase}/user/${id}`, {
      method: "DELETE",
      headers: this._h(),
      credentials: "include",
    });
    if (!response.ok) {
      const errorText = await response.text();
      throw new Error(errorText || "Khong the xoa user");
    }
  },

  // ── PRODUCTS ─────────────────────────────────────────────────
  async getProducts(page = 0, search = "", categoryId = "", stockStatus = "") {
    const params = new URLSearchParams({ page, size: 10 });
    if (search) params.set("search", search);
    if (categoryId) params.set("categoryId", categoryId);
    if (stockStatus) params.set("stockStatus", stockStatus);
    const response = await fetch(
      `${DB_CONFIG.apiBase}/api/products?${params}`,
      { headers: this._h(), credentials: "include" },
    );
    if (!response.ok) {
      throw new Error("Khong the tai danh sach san pham");
    }
    const data = await response.json();
    return {
      ...data,
      content: (data.content || []).map((p) => ({
        ...p,
        category_id: p.category_id ?? p.categoryId,
        rating_avg: p.rating_avg ?? p.ratingAvg ?? 0,
        rating_count: p.rating_count ?? p.ratingCount ?? 0,
        stock_quantity: p.stock_quantity ?? p.stockQuantity ?? 0,
      })),
    };
  },
  async getProductById(id) {
    const response = await fetch(`${DB_CONFIG.apiBase}/api/products/${id}`, {
      headers: this._h(),
      credentials: "include",
    });
    if (!response.ok) {
      throw new Error("Khong the tai san pham");
    }
    const p = await response.json();
    return {
      ...p,
      category_id: p.category_id ?? p.categoryId,
      short_description: p.short_description ?? p.shortDescription,
      description: p.description ?? p.description,
      old_price: p.old_price ?? p.oldPrice,
      rating_avg: p.rating_avg ?? p.ratingAvg ?? 0,
      rating_count: p.rating_count ?? p.ratingCount ?? 0,
      stock_quantity: p.stock_quantity ?? p.stockQuantity ?? 0,
      is_active: p.is_active ?? p.isActive,
    };
  },
  async createProduct(d) {
    const response = await fetch(`${DB_CONFIG.apiBase}/api/products`, {
      ...this._opts("POST", d),
    });
    if (!response.ok) {
      const errorText = await response.text();
      throw new Error(errorText || "Khong the tao san pham");
    }
    return response.json();
  },
  async updateProduct(id, d) {
    const response = await fetch(`${DB_CONFIG.apiBase}/api/products/${id}`, {
      ...this._opts("PUT", d),
    });
    if (!response.ok) {
      const errorText = await response.text();
      throw new Error(errorText || "Khong the cap nhat san pham");
    }
    return response.json();
  },
  async deleteProduct(id) {
    const response = await fetch(`${DB_CONFIG.apiBase}/api/products/${id}`, {
      method: "DELETE",
      headers: this._h(),
      credentials: "include",
    });
    if (!response.ok) {
      const errorText = await response.text();
      throw new Error(errorText || "Khong the xoa san pham");
    }
  },

  // ── CATEGORIES ──────────────────────────────────────────────
  async getCategories(page = 0, size = 10) {
    const response = await fetch(
      `${DB_CONFIG.apiBase}/api/categories?page=${page}&size=${size}`,
      { headers: this._h(), credentials: "include" },
    );
    if (!response.ok) {
      throw new Error("Khong the tai danh sach danh muc");
    }
    return response.json();
  },
  async createCategory(d) {
    const response = await fetch(`${DB_CONFIG.apiBase}/api/categories`, {
      ...this._opts("POST", d),
    });
    if (!response.ok) {
      const errorText = await response.text();
      throw new Error(errorText || "Khong the tao danh muc");
    }
    return response.json();
  },
  async updateCategory(id, d) {
    const response = await fetch(`${DB_CONFIG.apiBase}/api/categories/${id}`, {
      ...this._opts("PUT", d),
    });
    if (!response.ok) {
      const errorText = await response.text();
      throw new Error(errorText || "Khong the cap nhat danh muc");
    }
    return response.json();
  },
  async deleteCategory(id) {
    const response = await fetch(`${DB_CONFIG.apiBase}/api/categories/${id}`, {
      method: "DELETE",
      headers: this._h(),
      credentials: "include",
    });
    if (!response.ok) {
      const errorText = await response.text();
      throw new Error(errorText || "Khong the xoa danh muc");
    }
  },

  // ── REVIEWS ─────────────────────────────────────────────────
  async getReviews(page = 0) {
    const response = await fetch(
      `${DB_CONFIG.apiBase}/api/reviews?page=${page}&size=10`,
      { headers: this._h(), credentials: "include" },
    );
    if (!response.ok) {
      throw new Error("Khong the tai danh sach danh gia");
    }
    return response.json();
  },
  async deleteReview(id) {
    const response = await fetch(`${DB_CONFIG.apiBase}/api/reviews/${id}`, {
      method: "DELETE",
      headers: this._h(),
      credentials: "include",
    });
    if (!response.ok) {
      const errorText = await response.text();
      throw new Error(errorText || "Khong the xoa danh gia");
    }
  },

  // ── DISCOUNTS ────────────────────────────────────────────────
  async getDiscounts(page = 0) {
    const response = await fetch(
      `${DB_CONFIG.apiBase}/api/discounts?page=${page}&size=10`,
      { headers: this._h(), credentials: "include" },
    );
    if (!response.ok) {
      throw new Error("Khong the tai danh sach ma giam gia");
    }
    const data = await response.json();
    return {
      ...data,
      content: (data.content || []).map((d) => ({
        ...d,
        is_valid: d.is_valid ?? d.isValid,
        times_used: d.times_used ?? d.timesUsed ?? 0,
      })),
    };
  },
  async createDiscount(d) {
    const response = await fetch(`${DB_CONFIG.apiBase}/api/discounts`, {
      ...this._opts("POST", d),
    });
    if (!response.ok) {
      const errorText = await response.text();
      throw new Error(errorText || "Khong the tao ma giam gia");
    }
    return response.json();
  },
  async updateDiscount(id, d) {
    const response = await fetch(`${DB_CONFIG.apiBase}/api/discounts/${id}`, {
      ...this._opts("PUT", d),
    });
    if (!response.ok) {
      const errorText = await response.text();
      throw new Error(errorText || "Khong the cap nhat ma giam gia");
    }
    return response.json();
  },
  async deleteDiscount(id) {
    const response = await fetch(`${DB_CONFIG.apiBase}/api/discounts/${id}`, {
      method: "DELETE",
      headers: this._h(),
      credentials: "include",
    });
    if (!response.ok) {
      const errorText = await response.text();
      throw new Error(errorText || "Khong the xoa ma giam gia");
    }
  },

  // ── DASHBOARD STATS ──────────────────────────────────────────
  async getDashboardStats() {
    const response = await fetch(
      `${DB_CONFIG.apiBase}/api/dashboard/stats`,
      { headers: this._h(), credentials: "include" },
    );
    if (!response.ok) throw new Error("Cannot load dashboard stats");
    return response.json();
  },
};

/* ───────────────────────────────────────────────────────────────
   UTILS
─────────────────────────────────────────────────────────────── */
const Utils = {
  formatCurrency(n) {
    return new Intl.NumberFormat("vi-VN", {
      style: "currency",
      currency: "VND",
    }).format(n);
  },

  statusBadge(s) {
    const map = {
      completed: ["badge-success", "✓ Hoàn thành"],
      pending: ["badge-warning", "⏳ Chờ xác nhận"],
      processing: ["badge-info", "🔄 Xác nhận"],
      shipping: ["badge-info", "🚚 Đang giao"],
      cancelled: ["badge-danger", "✗ Đã hủy"],
    };
    const [cls, lbl] = map[s] || ["badge-gray", s];
    return `<span class="badge ${cls}">${lbl}</span>`;
  },

  roleBadge(r) {
    const normalizedRole = String(r || "").toUpperCase();
    return normalizedRole === "ADMIN"
      ? `<span class="badge badge-purple">👑 Admin</span>`
      : `<span class="badge badge-info">👤 User</span>`;
  },

  userStatusBadge(s) {
    const normalizedStatus = String(s || "").toUpperCase();
    return normalizedStatus === "ACTIVE"
      ? `<span class="badge badge-success">● Active</span>`
      : `<span class="badge badge-danger">● Inactive</span>`;
  },

  validBadge(v) {
    return v
      ? `<span class="badge badge-success">✓ Valid</span>`
      : `<span class="badge badge-danger">✗ Invalid</span>`;
  },

  stars(avg) {
    const f = Math.floor(avg);
    const h = avg % 1 >= 0.5;
    const s = "★".repeat(f) + (h ? "☆" : "") + "☆".repeat(5 - f - (h ? 1 : 0));
    return `<span class="stars">${s}</span> <small style="color:var(--text-muted);font-size:11px">${avg}</small>`;
  },

  toast(msg, type = "success") {
    let c = document.getElementById("toastContainer");
    if (!c) {
      c = document.createElement("div");
      c.id = "toastContainer";
      c.className = "toast-container";
      document.body.appendChild(c);
    }
    const t = document.createElement("div");
    const icons = { success: "✓", error: "✗", warn: "⚠" };
    t.className = `toast ${type}`;
    t.innerHTML = `<span>${icons[type] || "✓"}</span> ${msg}`;
    c.appendChild(t);
    setTimeout(() => {
      t.style.opacity = "0";
      t.style.transition = "opacity .3s";
      setTimeout(() => t.remove(), 300);
    }, 3000);
  },

  initChartBars() {
    document.querySelectorAll(".chart-bar").forEach((b) => {
      b.style.height = "0%";
    });
  },

  renderRevenueChart(revenueData) {
    const bars = document.querySelectorAll("#revenueChart .chart-bar");
    const labels = document.querySelectorAll(".chart-labels span");
    if (!bars.length) return;

    const max = Math.max(...revenueData.map((d) => d.revenue || 0), 1);

    revenueData.forEach((d, i) => {
      if (i < bars.length) {
        const pct = ((d.revenue || 0) / max) * 100;
        bars[i].style.height = Math.max(pct, 2) + "%";

        const dayNames = ["CN", "T2", "T3", "T4", "T5", "T6", "T7"];
        const date = new Date(d.date + "T00:00:00");
        bars[i].title = Utils.formatCurrency(d.revenue || 0);
        if (i < labels.length) {
          labels[i].textContent = dayNames[date.getDay()] || labels[i].textContent;
        }
      }
    });
  },

  renderPagination(containerId, page, totalPages, onChange) {
    const container = document.getElementById(containerId);
    if (!container) return;

    if (!totalPages || totalPages <= 1) {
      container.innerHTML = "";
      return;
    }

    const current = Number(page) || 0;
    const pages = [];

    const addBtn = (label, targetPage, disabled = false, active = false) => {
      const button = document.createElement("button");
      button.className = `page-btn${active ? " active" : ""}`;
      button.textContent = label;
      button.disabled = disabled;
      if (!disabled) {
        button.addEventListener("click", () => onChange(targetPage));
      }
      pages.push(button);
    };

    addBtn("<", current - 1, current <= 0, false);
    for (let i = 0; i < totalPages; i++) {
      addBtn(String(i + 1), i, false, i === current);
    }
    addBtn(">", current + 1, current >= totalPages - 1, false);

    container.innerHTML = "";
    pages.forEach((btn) => container.appendChild(btn));
  },
};

/* ───────────────────────────────────────────────────────────────
   DELETE CONFIRM — 10s countdown
─────────────────────────────────────────────────────────────── */
const DeleteConfirm = {
  _timer: null,
  _cb: null,

  show(label, onConfirm) {
    this._cb = onConfirm;
    const overlay = document.getElementById("deleteModalOverlay");
    const btn = document.getElementById("confirmDeleteBtn");
    const fill = document.getElementById("countdownFill");
    const lbl = document.getElementById("countdownLabel");
    const target = document.getElementById("deleteTargetLabel");

    if (!overlay) return;
    target.textContent = label;
    btn.disabled = true;

    fill.style.transition = "none";
    fill.style.width = "100%";
    lbl.textContent = "Vui lòng chờ 10 giây...";

    overlay.classList.add("open");

    let count = 10;
    setTimeout(() => {
      fill.style.transition = `width ${count}s linear`;
      fill.style.width = "0%";
    }, 60);

    clearInterval(this._timer);
    this._timer = setInterval(() => {
      count--;
      lbl.textContent =
        count > 0
          ? `Vui lòng chờ ${count} giây...`
          : "Bạn có thể xác nhận xóa bây giờ";
      if (count <= 0) {
        clearInterval(this._timer);
        btn.disabled = false;
      }
    }, 1000);
  },

  hide() {
    clearInterval(this._timer);
    const overlay = document.getElementById("deleteModalOverlay");
    if (overlay) overlay.classList.remove("open");
  },

  confirm() {
    if (this._cb) this._cb();
    this.hide();
  },
};

/* ───────────────────────────────────────────────────────────────
   MODAL HELPERS
─────────────────────────────────────────────────────────────── */
const Modal = {
  open(id) {
    const el = document.getElementById(id);
    if (el) {
      el.classList.add("open");
    }
  },
  close(id) {
    const el = document.getElementById(id);
    if (el) {
      el.classList.remove("open");
    }
  },
  closeAll() {
    document
      .querySelectorAll(".modal-overlay")
      .forEach((el) => el.classList.remove("open"));
  },
};

/* ───────────────────────────────────────────────────────────────
   LOGOUT / SESSION
─────────────────────────────────────────────────────────────── */
function logout() {
  if (!confirm("Bạn có chắc muốn đăng xuất?")) return;
  closeLogoutDropdown();

  fetch(`${APP_CONTEXT_PATH}/api/auth/admin/logout`, {
    method: "POST",
    credentials: "same-origin",
  })
    .then((response) => {
      if (!response.ok) {
        throw new Error("Logout failed");
      }
      localStorage.removeItem("authToken");
      Utils.toast("Đã đăng xuất thành công", "success");
      setTimeout(() => {
        window.location.href = `${APP_CONTEXT_PATH}/admin/login`;
      }, 300);
    })
    .catch(() => {
      Utils.toast("Không thể đăng xuất. Vui lòng thử lại.", "error");
    });
}

function toggleLogoutDropdown() {
  const menu = document.getElementById("logoutDropdown");
  if (!menu) return;
  menu.style.display = menu.style.display === "block" ? "none" : "block";
}

function closeLogoutDropdown() {
  const menu = document.getElementById("logoutDropdown");
  if (menu) menu.style.display = "none";
}

/* ───────────────────────────────────────────────────────────────
   ACTIVE NAV LINK  — call on each page after DOMContentLoaded
─────────────────────────────────────────────────────────────── */
function setActiveNav(page) {
  document.querySelectorAll(".nav-item[data-page]").forEach((el) => {
    el.classList.toggle("active", el.dataset.page === page);
  });
}

/* ───────────────────────────────────────────────────────────────
   ORDER BADGE — fetch pending order count for sidebar
─────────────────────────────────────────────────────────────── */
async function loadOrderBadge() {
  const badge = document.getElementById("navBadgeOrders");
  if (!badge) return;
  try {
    const stats = await API.getDashboardStats();
    badge.textContent = stats.pendingOrders ?? 0;
  } catch {
    badge.textContent = "?";
  }
}

/* ───────────────────────────────────────────────────────────────
   CLOSE MODAL ON OVERLAY CLICK
─────────────────────────────────────────────────────────────── */
document.addEventListener("DOMContentLoaded", () => {
  document.querySelectorAll(".modal-overlay").forEach((overlay) => {
    overlay.addEventListener("click", (e) => {
      if (e.target === overlay && overlay.id !== "deleteModalOverlay") {
        overlay.classList.remove("open");
      }
    });
  });

  document.addEventListener("click", (e) => {
    const dropdown = document.getElementById("logoutDropdown");
    const settingsBtn = document.getElementById("settingsBtn");
    if (!dropdown || !settingsBtn) return;
    if (settingsBtn.contains(e.target) || dropdown.contains(e.target)) return;
    dropdown.style.display = "none";
  });

  Utils.initChartBars();
  loadOrderBadge();
});
