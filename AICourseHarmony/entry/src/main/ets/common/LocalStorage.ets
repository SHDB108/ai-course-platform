import data_storage from '@ohos.data.storage';

let preferences: data_storage.Preferences | undefined;

async function getPrefs(context: any): Promise<data_storage.Preferences> {
  if (!preferences) {
    preferences = await data_storage.getPreferences(context, 'AICourseHarmonyData');
  }
  return preferences!;
}

/**
 * 保存数据到本地存储
 */
export async function saveData<T>(context: any, key: string, value: T): Promise<void> {
  const prefs = await getPrefs(context);
  await prefs.put(key, JSON.stringify(value));
  await prefs.flush();
}

export async function loadData<T>(context: any, key: string, defaultValue: T): Promise<T> {
  const prefs = await getPrefs(context);
  const str = await prefs.get(key, '');
  if (!str) return defaultValue;
  try {
    return JSON.parse(str) as T;
  } catch {
    return defaultValue;
  }
} 