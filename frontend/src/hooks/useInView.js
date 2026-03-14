import { useState, useEffect, useRef } from "react";

/**
 * Hook qui détecte quand un élément entre dans le viewport (Intersection Observer).
 * @param {Object} options - { rootMargin?: string, threshold?: number }
 * @returns {[React.RefObject, boolean]} - [ref à attacher à l'élément, isInView]
 */
export function useInView(options = {}) {
  const { rootMargin = "0px 0px -40px 0px", threshold = 0.1 } = options;
  const [isInView, setIsInView] = useState(false);
  const ref = useRef(null);

  useEffect(() => {
    const el = ref.current;
    if (!el) return;

    const observer = new IntersectionObserver(
      ([entry]) => {
        if (entry.isIntersecting) setIsInView(true);
      },
      { rootMargin, threshold }
    );
    observer.observe(el);
    return () => observer.disconnect();
  }, [rootMargin, threshold]);

  return [ref, isInView];
}
