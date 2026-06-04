(function () {
  var APIBASE = 'https://provinces.open-api.vn/api';
  var cache = {};

  var PROVINCES = [
    { code: 1, name: 'Thành phố Hà Nội' }, { code: 2, name: 'Tỉnh Hà Giang' },
    { code: 4, name: 'Tỉnh Cao Bằng' }, { code: 6, name: 'Tỉnh Bắc Kạn' },
    { code: 8, name: 'Tỉnh Tuyên Quang' }, { code: 10, name: 'Tỉnh Lào Cai' },
    { code: 11, name: 'Tỉnh Điện Biên' }, { code: 12, name: 'Tỉnh Lai Châu' },
    { code: 14, name: 'Tỉnh Sơn La' }, { code: 15, name: 'Tỉnh Yên Bái' },
    { code: 17, name: 'Tỉnh Hòa Bình' }, { code: 19, name: 'Tỉnh Thái Nguyên' },
    { code: 20, name: 'Tỉnh Lạng Sơn' }, { code: 22, name: 'Tỉnh Quảng Ninh' },
    { code: 24, name: 'Tỉnh Bắc Giang' }, { code: 25, name: 'Tỉnh Phú Thọ' },
    { code: 26, name: 'Tỉnh Vĩnh Phúc' }, { code: 27, name: 'Tỉnh Bắc Ninh' },
    { code: 30, name: 'Tỉnh Hải Dương' }, { code: 31, name: 'Thành phố Hải Phòng' },
    { code: 33, name: 'Tỉnh Hưng Yên' }, { code: 34, name: 'Tỉnh Thái Bình' },
    { code: 35, name: 'Tỉnh Hà Nam' }, { code: 36, name: 'Tỉnh Nam Định' },
    { code: 37, name: 'Tỉnh Ninh Bình' }, { code: 38, name: 'Tỉnh Thanh Hóa' },
    { code: 40, name: 'Tỉnh Nghệ An' }, { code: 42, name: 'Tỉnh Hà Tĩnh' },
    { code: 44, name: 'Tỉnh Quảng Bình' }, { code: 45, name: 'Tỉnh Quảng Trị' },
    { code: 46, name: 'Tỉnh Thừa Thiên Huế' }, { code: 48, name: 'Thành phố Đà Nẵng' },
    { code: 49, name: 'Tỉnh Quảng Nam' }, { code: 51, name: 'Tỉnh Quảng Ngãi' },
    { code: 52, name: 'Tỉnh Bình Định' }, { code: 54, name: 'Tỉnh Phú Yên' },
    { code: 56, name: 'Tỉnh Khánh Hòa' }, { code: 58, name: 'Tỉnh Ninh Thuận' },
    { code: 60, name: 'Tỉnh Bình Thuận' }, { code: 62, name: 'Tỉnh Kon Tum' },
    { code: 64, name: 'Tỉnh Gia Lai' }, { code: 66, name: 'Tỉnh Đắk Lắk' },
    { code: 67, name: 'Tỉnh Đắk Nông' }, { code: 68, name: 'Tỉnh Lâm Đồng' },
    { code: 70, name: 'Tỉnh Bình Phước' }, { code: 72, name: 'Tỉnh Tây Ninh' },
    { code: 74, name: 'Tỉnh Bình Dương' }, { code: 75, name: 'Tỉnh Đồng Nai' },
    { code: 77, name: 'Tỉnh Bà Rịa - Vũng Tàu' }, { code: 79, name: 'Thành phố Hồ Chí Minh' },
    { code: 80, name: 'Tỉnh Long An' }, { code: 82, name: 'Tỉnh Tiền Giang' },
    { code: 83, name: 'Tỉnh Bến Tre' }, { code: 84, name: 'Tỉnh Trà Vinh' },
    { code: 86, name: 'Tỉnh Vĩnh Long' }, { code: 87, name: 'Tỉnh Đồng Tháp' },
    { code: 89, name: 'Tỉnh An Giang' }, { code: 91, name: 'Tỉnh Kiên Giang' },
    { code: 92, name: 'Thành phố Cần Thơ' }, { code: 93, name: 'Tỉnh Hậu Giang' },
    { code: 94, name: 'Tỉnh Sóc Trăng' }, { code: 95, name: 'Tỉnh Bạc Liêu' },
    { code: 96, name: 'Tỉnh Cà Mau' }
  ];

  var provinceCodeByName = {
    'TP. Hồ Chí Minh': 79, 'TP Hồ Chí Minh': 79, 'Thành phố Hồ Chí Minh': 79, 'Hồ Chí Minh': 79,
    'Hà Nội': 1, 'Thành phố Hà Nội': 1, 'TP. Hà Nội': 1,
    'Đà Nẵng': 48, 'Thành phố Đà Nẵng': 48,
    'Cần Thơ': 92, 'Thành phố Cần Thơ': 92,
    'An Giang': 89, 'Bà Rịa - Vũng Tàu': 77, 'Bà Rịa Vũng Tàu': 77,
    'Bắc Giang': 24, 'Bắc Kạn': 6, 'Bạc Liêu': 95, 'Bắc Ninh': 27,
    'Bến Tre': 83, 'Bình Định': 52, 'Bình Dương': 74, 'Bình Phước': 70,
    'Bình Thuận': 60, 'Cà Mau': 96, 'Cao Bằng': 4, 'Đắk Lắk': 66,
    'Đắk Nông': 67, 'Điện Biên': 11, 'Đồng Nai': 75, 'Đồng Tháp': 87,
    'Gia Lai': 64, 'Hà Giang': 2, 'Hà Nam': 35, 'Hà Tĩnh': 42,
    'Hải Dương': 30, 'Hải Phòng': 31, 'Thành phố Hải Phòng': 31,
    'Hậu Giang': 93, 'Hòa Bình': 17, 'Hưng Yên': 33, 'Khánh Hòa': 56,
    'Kiên Giang': 91, 'Kon Tum': 62, 'Lai Châu': 12, 'Lâm Đồng': 68,
    'Lạng Sơn': 20, 'Lào Cai': 10, 'Long An': 80, 'Nam Định': 36,
    'Nghệ An': 40, 'Ninh Bình': 37, 'Ninh Thuận': 58, 'Phú Thọ': 25,
    'Phú Yên': 54, 'Quảng Bình': 44, 'Quảng Nam': 49, 'Quảng Ngãi': 51,
    'Quảng Ninh': 22, 'Quảng Trị': 45, 'Sóc Trăng': 94, 'Sơn La': 14,
    'Tây Ninh': 72, 'Thái Bình': 34, 'Thái Nguyên': 19, 'Thanh Hóa': 38,
    'Thừa Thiên Huế': 46, 'Tiền Giang': 82, 'Trà Vinh': 84, 'Tuyên Quang': 8,
    'Vĩnh Long': 86, 'Vĩnh Phúc': 26, 'Yên Bái': 15
  };

  function initAddressSelects() {
    var cityEl = document.getElementById('addrCity');
    var provinceEl = document.getElementById('addrProvince');
    var wardEl = document.getElementById('addrWard');
    if (!cityEl) return;

    populateProvinces(PROVINCES);

    fetch(APIBASE + '/')
      .then(function (r) { return r.json(); })
      .then(function (data) {
        if (data && data.length) populateProvinces(data);
      })
      .catch(function () {});

    cityEl.addEventListener('change', function () {
      var opt = this.options[this.selectedIndex];
      var code = opt ? opt.getAttribute('data-code') : null;
      if (!code) {
        clearSelect(provinceEl);
        clearSelect(wardEl);
        return;
      }
      if (cache[code]) {
        populateDistricts(cache[code].districts || []);
        return;
      }
      fetch(APIBASE + '/p/' + code + '?depth=3')
        .then(function (r) { return r.json(); })
        .then(function (data) {
          cache[code] = data;
          populateDistricts(data.districts || []);
        })
        .catch(function () {
          switchToTextFallback('addrProvince', 'Nhập quận/huyện');
          switchToTextFallback('addrWard', 'Nhập phường/xã');
        });
    });

    provinceEl.addEventListener('change', function () {
      var opt = this.options[this.selectedIndex];
      var code = opt ? opt.getAttribute('data-code') : null;
      if (!code) {
        clearSelect(wardEl);
        return;
      }
      var cityOpt = cityEl.options[cityEl.selectedIndex];
      var cityCode = cityOpt ? cityOpt.getAttribute('data-code') : null;
      var cityData = cache[cityCode];
      if (cityData && cityData.districts) {
        var dist = cityData.districts.find(function (d) { return String(d.code) === code; });
        if (dist && dist.wards && dist.wards.length) populateWards(dist.wards);
        else clearSelect(wardEl);
      } else {
        clearSelect(wardEl);
      }
    });
  }

  function populateProvinces(list) {
    var el = document.getElementById('addrCity');
    if (!el) return;
    el.innerHTML = '<option value="">-- Chọn tỉnh/thành --</option>';
    list.forEach(function (p) {
      var opt = document.createElement('option');
      opt.value = p.name;
      opt.textContent = p.name;
      opt.setAttribute('data-code', p.code);
      el.appendChild(opt);
    });
  }

  function populateDistricts(districts) {
    var el = document.getElementById('addrProvince');
    if (!el) return;
    el.innerHTML = '<option value="">-- Chọn quận/huyện --</option>';
    if (!districts || !districts.length) return;
    districts.forEach(function (d) {
      var opt = document.createElement('option');
      opt.value = d.name;
      opt.textContent = d.name;
      opt.setAttribute('data-code', d.code);
      el.appendChild(opt);
    });
    clearSelect(document.getElementById('addrWard'));
  }

  function populateWards(wards) {
    var el = document.getElementById('addrWard');
    if (!el) return;
    el.innerHTML = '<option value="">-- Chọn phường/xã --</option>';
    if (!wards || !wards.length) return;
    wards.forEach(function (w) {
      var opt = document.createElement('option');
      opt.value = w.name;
      opt.textContent = w.name;
      el.appendChild(opt);
    });
  }

  function clearSelect(el) {
    if (!el) return;
    el.innerHTML = '<option value="">-- Chọn --</option>';
  }

  function switchToTextFallback(elId, placeholder) {
    var el = document.getElementById(elId);
    if (!el || el.tagName !== 'SELECT') return;
    var parent = el.parentElement;
    var input = document.createElement('input');
    input.type = 'text';
    input.className = el.className;
    input.name = el.name;
    input.id = el.id;
    input.required = el.required;
    input.placeholder = placeholder;
    input.autocomplete = 'off';
    parent.replaceChild(input, el);
  }

  function setAddressByNames(cityName, districtName, wardName) {
    var cityEl = document.getElementById('addrCity');
    var provinceEl = document.getElementById('addrProvince');
    var wardEl = document.getElementById('addrWard');
    if (!cityEl) return;

    var found = false;
    for (var i = 0; i < cityEl.options.length; i++) {
      if (cityEl.options[i].value === cityName) {
        cityEl.selectedIndex = i;
        found = true;
        break;
      }
    }
    if (!found) {
      var code = provinceCodeByName[cityName];
      if (code) {
        for (var j = 0; j < cityEl.options.length; j++) {
          if (cityEl.options[j].getAttribute('data-code') == code) {
            cityEl.selectedIndex = j;
            found = true;
            break;
          }
        }
      }
    }
    if (!found) return;

    if (districtName && provinceEl && provinceEl.tagName === 'INPUT') {
      provinceEl.value = districtName;
    }
    if (wardName && wardEl && wardEl.tagName === 'INPUT') {
      wardEl.value = wardName;
    }

    var evt = new Event('change', { bubbles: true });
    cityEl.dispatchEvent(evt);

    var checkDistricts = setInterval(function () {
      if (provinceEl && provinceEl.tagName === 'INPUT') {
        clearInterval(checkDistricts);
        return;
      }
      if (provinceEl.options.length > 1) {
        clearInterval(checkDistricts);
        for (var i = 0; i < provinceEl.options.length; i++) {
          if (provinceEl.options[i].value === districtName) {
            provinceEl.selectedIndex = i;
            var de = new Event('change', { bubbles: true });
            provinceEl.dispatchEvent(de);
            var checkWards = setInterval(function () {
              if (wardEl && wardEl.tagName === 'INPUT') {
                clearInterval(checkWards);
                return;
              }
              if (wardEl.options.length > 1) {
                clearInterval(checkWards);
                for (var j = 0; j < wardEl.options.length; j++) {
                  if (wardEl.options[j].value === wardName) {
                    wardEl.selectedIndex = j;
                    break;
                  }
                }
              }
            }, 100);
            break;
          }
        }
      }
    }, 100);
  }

  window.initAddressSelects = initAddressSelects;
  window.setAddressByNames = setAddressByNames;
})();
