const routeName = 'share'
const Layout = () => import('@/layout/index.vue')
const src = [
  'https://nd-static.bdstatic.com/m-static/v20-main/home/img/icon-share-active.8080707e.png',
  'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAEgAAABICAYAAAEi6oPRAAAABGdBTUEAALGPC/xhBQAABfRJREFUeAHtW11oHEUcn//uJTZabBC1gvoixq+XpMlGxYLap+QuVKgmV+2D0hcfxAcVbKHSKn1Qiw8+Cj6oRVC4qA/au6j4ELQgkg8/oGiJkIIKwYhtNZ7G3O34m7OT7m12Z/dm9zYxzkIyH/+P+c1v5mZn54Oxtjyl8hQXf37nJDMChUSTYwVnlyWVvKFlsRGR5pzfK8I1SkT0tM3ptBDKZ40SrF9a4fx7qbDhQ/BUC+JKAFeSWRwZbMjXUMBs+2F/tZuUhGWOsW/8Ssi7+AhMtXr9Ysb/ILbKt6xrWMNYljU8mh/4SOr5w9iO/IYiLRtWxJuaRGR4H9umgQeGnVlvXhhipaN6nc/AEP2NvuCM3+F16I839SS/UKYDnRCtSLkJ/6sMrOnZ4+Xpt9DcD8kKYZT+GGP8kEyHhU2O/E68Rt6fgzdfxpschXV/qewN/Y5j9Wyvg7B4LEf+0oOcKR0JB9KJDIOciLxQR7lLtl75TnnWGa9MvygUo/gLHUZqy0u/CAcMMwI4OdiIK/6FIlLYBIqaHOVsuy9Qy2QaBgwDG4KBpvE6CFHUGNRkk+u8oTjUO9+U12JCCQhgavBnt+gzVJ2Y9fLYyMBToQoQNA1pAYqpgRG+OXOfjGI8ClAAxpAsfJpgklkKkcbODn0NxfFAxJ4fKww+49F9E/G9lcrc5Uv83HlPfuyoJiD6tjji3BZWim1X60z0Po1HD9ClXbf7+wLYegT9f5HzeuV87W8NKP+a6AGqVn/3l8g5O85Y8g/TiE5NP/kLbndaCQj95DpMrY+kBkJ8jRUcvVZJDYRxZBgwDBgGDAOGgfYyoJzki6Lf+3C2t1Z3y5gRXxsEBS/fn4n4E6P5wbeD5K3mKQFhOeYwdjKOxnGK+fTc2IhzUxxdlY5y+hEXjCgAq4E94+Wp31SFxZEpGfJPU+M4DNURc6Hu7duKd13/Z6gOBEqGVIYtyzjvYGcXqmBdSUJ2gC7UYHxi5jNVZTIHhN3CnZkA6tpOl6kKiitLjaHdjlNtLPsSaX2xSsDagDAgLm/LdW71rkMLp8WC0w3ZCVlAq6EeIGJ/Yddhy9BQ7x9BBUK2G9tbHwTJovK0vpHG8o6yv5TK0yfQeRs73lEA/HINQOSiSdzSqVOd7IflveS6PRhbtqDpDgjnpcr0HMDc6C8obrp1QMTLpcrUJDtTvUcUgk0D+RwAmCWAUbInlcPC1gExthMorvA7bLxmsOKQ9Gm9UweASQrCa986IK91G+LrAEi9xKMG1IbN7JxtKYcDNaD8QBdLERSGiyP3D/d/3YaWNi4NA4YBw4BhwDBgGDAMGAYMA4YBw4BhICEDyrXiKN8lzm2amDmEtY7DWH7piNKPKf+Ekb2vWOhfjKnfVjVtghqbQ647lSIx/oouWNRRGC30fekXZJnWJgjLmT+G7ZylXgFi7+IY3UmADVzrleVh2a6GJZGFXBd9vmfXjnMyP0mYgKC1d6WSAGmLLbHTHZQb3pPfcUbXv3qRStfrRrHj7OYVtzZfmph5VBfS5iboAivk8mObhyCiZ3EXL9WlUOyRd28egixrHucT+8ROIE4jHAJZrm7l0rBb358YsV/BwmtkUdHK0Z3MtnqY7b4vK4ajGi+ALJtZnbeArO9kfpahzvZYQnx0Fscn7ivmnZNxHRXzveKq763YqLTGJ2aPMu56j2zHdaOlly1BRI9h0/wVLaQwmpwEtZxfo2uvY5cdQZa1v5gfeEMHZKkyexVOipcXq9ODOvZJbLIi6CtBTuPbrTLzHAA/HvpmwdWL4nC/uOnAxI0/Tm4ZZ/qvFun1eDKZSWMgfh2V68KnwINRlWxcdOCWjZfXq9BPrQEbh3SiCg+QpwYgwPdqFiq6fzUREYHuccbwZk9+HCKipHji9X3Nx8O4rlpJCEp+vya7qmtj1SYojTtsWfGTBKs2QZjl7hNHSLKqpG45AqPAqm2va+i1E6uL9bp7EOPq3RhdxUQu1Sue3rIi4vgp0QJezZ/atnXMnP+JYMuIDQOGAcOAYcAwYBgwDCgY+Ady3KRgPUwx3gAAAABJRU5ErkJggg=='
]
const routes = [
  {
    path: '/share',
    name: routeName,
    redirect: '/share/record',
    component: Layout,
    meta: {
      title: '收发',
      sort: 1,
      src: src,
      alwaysShow: true
    },
    children: [
      {
        path: '/share/record',
        name: `${routeName}_category`,
        meta: {
          title: '分享记录',
          target: '/index'
        },
        component: () => import('@/views/share/share.vue')
      }
    ]
  }
]

export default routes
