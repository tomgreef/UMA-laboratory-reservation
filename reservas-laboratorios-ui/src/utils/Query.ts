import { useCallback, useEffect, useState } from "react";

interface QueryOptions<V> {
  variables?: V;
  skip?: boolean;
}

export type QueryResult<T> = {
  data?: T;
  loading: boolean;
  error?: string;
};
const BASE_URL = import.meta.env.VITE_API_URL + ":" + import.meta.env.VITE_API_PORT + "/";

export function useCustomQuery<T, V = undefined>(path: string, options?: QueryOptions<V>): QueryResult<T> & { refetch: () => void } {
  const [data, setData] = useState<T>();
  const { variables, skip } = options || {};
  const [loading, setLoading] = useState(skip ? false : true);
  const [error, setError] = useState<string>();
  const [localVariables, setVariables] = useState<V | undefined>(variables);

  const fetchData = useCallback(() => {
    if (skip) return;
    setError(undefined);
    setLoading(true);

    const url = new URL(BASE_URL + path);
    if (variables) {
      Object.entries(variables).forEach(([key, value]) => {
        url.searchParams.append(key, String(value));
      });
    }

    // console.log("Fetching data from " + BASE_URL + path + " with variables " + JSON.stringify(variables));

    fetch(url.toString())
      .then((response: Response) => {
        if (response.ok) {
          return response.json();
        }
        throw response;
      })
      .then((responseData) => {
        if (typeof responseData.data === "boolean") {
          setData(responseData.data);
          return;
        }
        setData(responseData.data || responseData);
      })
      .catch(async (response) => {
        try {
          response = await response.json();
        } catch (e) {
          // Ignore
        }
        setError(response.error || response.message || "Unknown error");
      })
      .finally(() => {
        setLoading(false);
      });
  }, [path, variables]);

  useEffect(() => {
    if (!skip) {
      fetchData();
    }
  }, []);

  useEffect(() => {
    if (!skip && !data && localVariables !== variables && !error) {
      setVariables(variables);
      fetchData();
    }
  }, [skip]);

  return { data, loading, error, refetch: fetchData };
}
