import { createPinia } from 'pinia'
const store = createPinia()

export function setupStore(app) {
  app.config.devtools = true
  app.use(store)
}

export { store }
