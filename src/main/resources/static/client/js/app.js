/**
 * WineStore - Main App JS
 * Handles: Cart, Contact Bubble, Filter, Toast Notifications
 */

// ===== CART STATE =====
const Cart = (() => {
  let items = JSON.parse(localStorage.getItem("winestore_cart") || "[]");

  const save = () =>
    localStorage.setItem("winestore_cart", JSON.stringify(items));

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
  const FILTER_KEYS = [
    "price",
    "category",
    "sort",
    "q",
  ];

  const getParams = () => {
    const params = new URLSearchParams();
    // Price: single-choice
    const selectedPrice = document.querySelector('input[name="price"]:checked');
    if (selectedPrice) params.set("price", selectedPrice.value);
    // Category: single-choice
    const selectedCategory = document.querySelector('input[name="category"]:checked');
    if (selectedCategory && selectedCategory.value) params.set("category", selectedCategory.value);
    // Sort
    const sort = document.querySelector(".sort-select");
    if (sort && sort.value && sort.value !== "default") {
      params.set("sort", sort.value);
    }
    // Search keyword
    const kw = document.querySelector(".search-input");
    if (kw && kw.value.trim()) params.set("q", kw.value.trim());
    return params;
  };

  const buildShopUrl = (params) => {
    const url = new URL(window.location.origin + window.location.pathname);
    params.forEach((value, key) => url.searchParams.append(key, value));
    return url;
  };

  const updateResults = () => {
    const grid = document.querySelector("#product-grid");
    if (!grid) return;

    // Show loading state
    grid.style.opacity = "0.5";

    const params = getParams();
    const url = buildShopUrl(params);
    window.location.href = url.toString();
  };

  const init = () => {
    const filterForm = document.querySelector("#filter-form");
    if (!filterForm) return;

    // Normalize legacy URLs that may contain multiple checked price values.
    const priceChecked = filterForm.querySelectorAll(
      'input[name="price"]:checked',
    );
    if (priceChecked.length > 1) {
      priceChecked.forEach((el, idx) => {
        if (idx > 0) el.checked = false;
      });
    }

    filterForm.querySelectorAll('input[type="checkbox"]').forEach((cb) => {
      cb.addEventListener("change", () => {
        if (cb.name === "price" && cb.checked) {
          filterForm.querySelectorAll('input[name="price"]').forEach((el) => {
            if (el !== cb) el.checked = false;
          });
        }
        if (cb.name === "category" && cb.checked) {
          filterForm.querySelectorAll('input[name="category"]').forEach((el) => {
            if (el !== cb) el.checked = false;
          });
        }
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
    const isLoggedIn = document.querySelector('.user-dropdown') !== null;
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
