export const CONSTANT = {
  configs: {
    timeZone: '-04:00',
  },
  authToken: 'authToken',
  supportedLanguages: ['vi_VN', 'en_US', 'ko_KR'],
  defaultLocale: 'vi_VN',
  defaultFormatDatetime: 'yyyy-MM-dd hh:mm:ss',
  defaultFormatDate: 'yyyy-MM-dd',
  adminPath: '/admin',
  loginPath: '/login-register',
  login: 'login-register',
  register: 'register',
  BE_URL_LOCAL: 'http://localhost:1212',
  bearer: 'Bearer ',
  defaultNumberQuestion: 1,
  defaultLibAdminLv1: ['../assets/vendor/mdi/css/materialdesignicons.min.css', '../assets/vendors/css/vendor.bundle.base.css', '../assets/css/style.css'],
  defaultLibAdminLv2: ['../../assets/vendor/mdi/css/materialdesignicons.min.css', '../../assets/vendors/css/vendor.bundle.base.css', '../../assets/css/style.css'],
  defaultLibAdminLv3: ['../../../../../share/styles/css/app.min.scss', '../../../../../share/styles/css/icons.min.scss'],
};

export const LIB = {
  user: {
    lib1: [
      '/app/share/styles/css/bootstrap.scss',
      '/app/share/styles/css/responsive.scss',
      '/app/share/styles/css/font-awesome.scss',
      '/app/share/styles/css/flaticon.scss',
      '/app/share/styles/css/fancybox.scss',
      '/app/share/styles/css/color.scss',
      '/app/share/styles/css/style.scss',
    ],
    lib2: ['../../../share/styles/css/user-style.scss'],
    script1: [
      "/app/share/styles/script/bootstrap.min.js",
      // "/app/share/styles/script/jquery.countdown.min.js",
      // "/app/share/styles/script/fancybox.pack.js",
      // "/app/share/styles/script/isotope.min.js",
      // "/app/share/styles/script/progressbar.js",
      // "/app/share/styles/script/counter.js",
    ]
  },
  admin: {
    style: [
      '/app/share/styles/css/admin/vendor.bundle.base.css',
      '/app/share/styles/css/admin/materialdesignicons.min.css',
      '/app/share/styles/css/admin/style.css',
    ],
    script: [
      '/app/share/styles/script/admin/vendor.bundle.base.js',
    ]
  }
}

export const CHART_OPTIONS = {
  init: {
    labels: [],
    datasets: [{data: [], label: ''}]
  },
  bar: {
    scales: {
      x: {},
      y: {
        min: 0,
      },
    },
    plugins: {
      legend: {
        display: true,
      },
      datalabels: {
        anchor: 'end',
        align: 'end',
      },
    },
  }
};

export const API_URL = {
  authPrefix: '/auth/',
  translatePath: './assets/i18n/',
  auth: {
    info: '/api/users/info',
    login: '/api/auth/login',
    register: '/api/auth/register',
    logout: '/api/auth/logout',
    refreshToken: '/api/auth/refresh-token',
    captcha: '/api/auth/captcha?t={0}',
  }
};

