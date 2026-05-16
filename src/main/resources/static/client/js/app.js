/**
 * VitaStore - Main App JS
 * Handles: Cart, Contact Bubble, Filter, Toast Notifications
 */

// ===== CART STATE =====
const Cart = (() => {
  let items = JSON.parse(localStorage.getItem("vitastore_cart") || "[]");

  const save = () =>
    localStorage.setItem("vitastore_cart", JSON.stringify(items));

  const getCount = () => items.reduce((s, i) => s + i.qty, 0);

  const add = (id, name, price, img, brand) => {
    const existing = items.find((i) => i.id === id);
    if (existing) {
      existing.qty++;
    } else {
      items.push({ id, name, price, img, brand, qty: 1 });
    }
    save();
    updateBadge();
    Toast.show(`Đã thêm "${name}" vào giỏ hàng`, "success");
  };

  const remove = (id) => {
    items = items.filter((i) => i.id !== id);
    save();
    updateBadge();
  };

  const updateQty = (id, qty) => {
    const item = items.find((i) => i.id === id);
    if (item) {
      item.qty = Math.max(1, qty);
      save();
    }
  };

  const getTotal = () => items.reduce((s, i) => s + i.price * i.qty, 0);

  const getItems = () => [...items];

  const clear = () => {
    items = [];
    save();
    updateBadge();
  };

  const updateBadge = () => {
    document.querySelectorAll(".cart-badge").forEach((el) => {
      const c = getCount();
      el.textContent = c;
      el.style.display = c > 0 ? "flex" : "none";
    });
  };

  return {
    add,
    remove,
    updateQty,
    getTotal,
    getItems,
    getCount,
    clear,
    updateBadge,
  };
})();

// ===== TOAST =====
const Toast = (() => {
  const getContainer = () => {
    let c = document.querySelector(".toast-container");
    if (!c) {
      c = document.createElement("div");
      c.className = "toast-container";
      document.body.appendChild(c);
    }
    return c;
  };

  const show = (msg, type = "") => {
    const t = document.createElement("div");
    t.className = `toast ${type}`;
    const icon = type === "success" ? "✓" : type === "error" ? "✕" : "ℹ";
    t.innerHTML = `<span>${icon}</span><span>${msg}</span>`;
    getContainer().appendChild(t);
    setTimeout(() => t.remove(), 3200);
  };

  return { show };
})();

// ===== CONTACT BUBBLE =====
const ContactBubble = (() => {
  const init = () => {
    const toggle = document.querySelector(".contact-toggle");
    const popup = document.querySelector(".contact-popup");
    if (!toggle || !popup) return;

    toggle.addEventListener("click", () => {
      popup.classList.toggle("open");
    });

    // Close on outside click
    document.addEventListener("click", (e) => {
      if (!e.target.closest(".contact-bubble-wrap")) {
        popup.classList.remove("open");
      }
    });

    const closeBtn = popup.querySelector(".contact-popup-close");
    if (closeBtn)
      closeBtn.addEventListener("click", () => popup.classList.remove("open"));
  };
  return { init };
})();

// ===== FILTER (AJAX) =====
const ShopFilter = (() => {
  let debounceTimer;

  const getParams = () => {
    const params = new URLSearchParams();
    // Price checkboxes
    document
      .querySelectorAll('input[name="price"]:checked')
      .forEach((el) => params.append("price", el.value));
    // Category checkboxes
    document
      .querySelectorAll('input[name="category"]:checked')
      .forEach((el) => params.append("category", el.value));
    // Brand checkboxes
    document
      .querySelectorAll('input[name="brand"]:checked')
      .forEach((el) => params.append("brand", el.value));
    // Target
    document
      .querySelectorAll('input[name="target"]:checked')
      .forEach((el) => params.append("target", el.value));
    // Sort
    const sort = document.querySelector(".sort-select");
    if (sort) params.set("sort", sort.value);
    // Search keyword
    const kw = document.querySelector(".search-input");
    if (kw && kw.value.trim()) params.set("q", kw.value.trim());
    return params;
  };

  const updateResults = () => {
    const grid = document.querySelector("#product-grid");
    const countEl = document.querySelector("#result-count");
    if (!grid) return;

    // Show loading state
    grid.style.opacity = "0.5";

    const params = getParams();
    fetch(`/api/products?${params.toString()}`)
      .then((res) => res.json())
      .then((data) => {
        // Server will re-render; here we update via Thymeleaf fragment or full page reload
        // For SPA-style: inject returned HTML snippet
        if (data.html) {
          grid.innerHTML = data.html;
          grid.style.opacity = "1";
        } else {
          // fallback: reload with params
          window.location.search = params.toString();
        }
        if (countEl && data.total !== undefined) {
          countEl.textContent = data.total;
        }
      })
      .catch(() => {
        // fallback: reload page with new query params
        const url = new URL(window.location.href);
        params.forEach((v, k) => url.searchParams.set(k, v));
        window.location.href = url.toString();
      })
      .finally(() => {
        grid.style.opacity = "1";
      });
  };

  const init = () => {
    const filterForm = document.querySelector("#filter-form");
    if (!filterForm) return;

    filterForm.querySelectorAll('input[type="checkbox"]').forEach((cb) => {
      cb.addEventListener("change", () => {
        clearTimeout(debounceTimer);
        debounceTimer = setTimeout(updateResults, 400);
      });
    });

    const sortSelect = document.querySelector(".sort-select");
    if (sortSelect) sortSelect.addEventListener("change", updateResults);

    // Clear all filters
    const clearBtn = document.querySelector(".filter-clear");
    if (clearBtn) {
      clearBtn.addEventListener("click", () => {
        filterForm
          .querySelectorAll('input[type="checkbox"]')
          .forEach((cb) => (cb.checked = false));
        if (sortSelect) sortSelect.value = "default";
        updateResults();
      });
    }

    // Collapsible filter sections
    document.querySelectorAll(".filter-section-title").forEach((title) => {
      title.addEventListener("click", () => {
        title.classList.toggle("collapsed");
        const options = title.nextElementSibling;
        if (options)
          options.style.display = title.classList.contains("collapsed")
            ? "none"
            : "";
      });
    });
  };

  return { init, updateResults };
})();

// ===== QTY CONTROLS =====
const QtyControl = (() => {
  const init = () => {
    document.addEventListener("click", (e) => {
      if (e.target.closest(".qty-btn")) {
        const btn = e.target.closest(".qty-btn");
        const input = btn.parentElement.querySelector(
          ".qty-input, .detail-qty-num",
        );
        if (!input) return;
        let val = parseInt(input.value) || 1;
        val = btn.dataset.action === "minus" ? Math.max(1, val - 1) : val + 1;
        input.value = val;
        input.dispatchEvent(new Event("change"));
      }
      if (e.target.closest(".detail-qty-btn")) {
        const btn = e.target.closest(".detail-qty-btn");
        const input = btn.parentElement.querySelector(".detail-qty-num");
        if (!input) return;
        let val = parseInt(input.value) || 1;
        val = btn.dataset.action === "minus" ? Math.max(1, val - 1) : val + 1;
        input.value = val;
      }
    });
  };
  return { init };
})();

// ===== ADD TO CART BUTTONS =====
const initAddToCartButtons = () => {
  document.addEventListener("click", (e) => {
    const btn = e.target.closest("[data-add-cart]");
    if (!btn) return;
    const id = btn.dataset.id;
    const name = btn.dataset.name;
    const price = parseFloat(btn.dataset.price);
    const img = btn.dataset.img || "";
    const brand = btn.dataset.brand || "";
    Cart.add(id, name, price, img, brand);

    // Animate button
    btn.classList.add("adding");
    setTimeout(() => btn.classList.remove("adding"), 800);
  });
};

// ===== DETAIL PAGE TABS =====
const initDetailTabs = () => {
  document.querySelectorAll(".detail-tab").forEach((tab) => {
    tab.addEventListener("click", () => {
      document
        .querySelectorAll(".detail-tab")
        .forEach((t) => t.classList.remove("active"));
      document
        .querySelectorAll(".tab-content")
        .forEach((c) => c.classList.remove("active"));
      tab.classList.add("active");
      const target = document.querySelector(`#${tab.dataset.tab}`);
      if (target) target.classList.add("active");
    });
  });
};

// ===== GALLERY THUMBS =====
const initGallery = () => {
  document.querySelectorAll(".thumb-img").forEach((thumb) => {
    thumb.addEventListener("click", () => {
      document
        .querySelectorAll(".thumb-img")
        .forEach((t) => t.classList.remove("active"));
      thumb.classList.add("active");
      const mainImg = document.querySelector(".main-image img");
      if (mainImg && thumb.dataset.src) mainImg.src = thumb.dataset.src;
    });
  });
};

// ===== CHECKOUT - LOGIN GUARD =====
const initCheckoutGuard = () => {
  const checkoutBtn = document.querySelector("[data-checkout]");
  if (!checkoutBtn) return;
  checkoutBtn.addEventListener("click", (e) => {
    const isLoggedIn = document.body.dataset.loggedIn === "true";
    if (!isLoggedIn) {
      e.preventDefault();
      const overlay = document.querySelector(".login-required-overlay");
      if (overlay) overlay.style.display = "flex";
    }
  });

  const overlay = document.querySelector(".login-required-overlay");
  if (overlay) {
    const closeBtn = overlay.querySelector("[data-modal-close]");
    if (closeBtn)
      closeBtn.addEventListener(
        "click",
        () => (overlay.style.display = "none"),
      );
    overlay.addEventListener("click", (e) => {
      if (e.target === overlay) overlay.style.display = "none";
    });
  }
};

// ===== PAYMENT OPTIONS =====
const initPaymentOptions = () => {
  document.querySelectorAll(".payment-option").forEach((opt) => {
    opt.addEventListener("click", () => {
      document
        .querySelectorAll(".payment-option")
        .forEach((o) => o.classList.remove("selected"));
      opt.classList.add("selected");
      const radio = opt.querySelector('input[type="radio"]');
      if (radio) radio.checked = true;
    });
  });
};

// ===== INIT ALL =====
document.addEventListener("DOMContentLoaded", () => {
  Cart.updateBadge();
  ContactBubble.init();
  ShopFilter.init();
  QtyControl.init();
  initAddToCartButtons();
  initDetailTabs();
  initGallery();
  initCheckoutGuard();
  initPaymentOptions();
});
