export const getConfigFileName = (env) => {
  return `__PRODUCTION__${env.VITE_GLOB_APP_SHORT_NAME || '__APP'}__CONF__`
    .toUpperCase()
    .replace(/\s/g, '')
}

export function getAppEnvConfig() {
  const ENV_NAME = getConfigFileName(import.meta.env)

  const ENV = import.meta.env.DEV
    ? // Get the global configuration (the configuration will be extracted independently when packaging)
      import.meta.env
    : window[ENV_NAME]

  const {
    VITE_GLOB_APP_TITLE,
    VITE_GLOB_API_URL,
    VITE_GLOB_APP_SHORT_NAME,
    VITE_GLOB_API_URL_PREFIX,
    VITE_GLOB_UPLOAD_URL,
    VITE_GLOB_IMG_URL,
    VITE_GLOB_AVATAR_URL
  } = ENV

  if (!/^[a-zA-Z\_]*$/.test(VITE_GLOB_APP_SHORT_NAME)) {
    console.log(
      `VITE_GLOB_APP_SHORT_NAME Variables can only be characters/underscores, please modify in the environment variables and re-running.`
    )
  }
  return {
    VITE_GLOB_APP_TITLE,
    VITE_GLOB_API_URL,
    VITE_GLOB_APP_SHORT_NAME,
    VITE_GLOB_API_URL_PREFIX,
    VITE_GLOB_UPLOAD_URL,
    VITE_GLOB_IMG_URL,
    VITE_GLOB_AVATAR_URL
  }
}

/**
 * @description: Development model
 */
export const devMode = 'development'

/**
 * @description: Production mode
 */
export const prodMode = 'production'

/**
 * @description: Is it a development mode
 * @returns:
 * @example:
 */
export function isDevMode() {
  return import.meta.env.DEV
}

/**
 * @description: Is it a production mode
 * @returns:
 * @example:
 */
export function isProdMode() {
  return import.meta.env.PROD
}
