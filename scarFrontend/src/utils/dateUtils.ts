/**
 * Convert datetime-local input value to ISO string with Korea timezone (UTC+9)
 * @param datetimeLocal - Value from datetime-local input (e.g., "2025-12-13T09:30")
 * @returns ISO string with timezone (e.g., "2025-12-13T09:30:00+09:00")
 */
export function toKSTISOString(datetimeLocal: string): string {
    // datetime-local format: "YYYY-MM-DDTHH:mm"
    // We need to add seconds and timezone offset for Korea (UTC+9)
    return `${datetimeLocal}:00+09:00`;
}

/**
 * Convert ISO string to datetime-local input value
 * @param isoString - ISO datetime string
 * @returns datetime-local format (e.g., "2025-12-13T09:30")
 */
export function fromISOStringToLocal(isoString: string): string {
    // Extract just the date and time part (YYYY-MM-DDTHH:mm)
    return isoString.substring(0, 16);
}
