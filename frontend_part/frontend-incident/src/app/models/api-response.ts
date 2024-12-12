export interface ApiResponseGenericPagination<T> {
  currentPage: number;
  pageSize: number;
  totalPages: number;
  list: T[];
}
